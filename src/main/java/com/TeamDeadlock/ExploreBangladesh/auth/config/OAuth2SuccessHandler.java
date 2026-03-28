package com.TeamDeadlock.ExploreBangladesh.auth.config;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.Provider;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.RefreshToken;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.Role;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.User;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.RefreshTokenRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.RoleRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.service.impl.CookieService;
import com.TeamDeadlock.ExploreBangladesh.auth.service.impl.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.auth.frontend.success-redirect}")
    private String frontEndSuccessUrl;


    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            logger.info("======= OAuth2 Authentication Started =======");
            logger.info("Authentication details: {}", authentication.toString());

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            if (oAuth2User == null) {
                throw new RuntimeException("OAuth2User principal is null");
            }

            String registrationId = "unknown";
            if (authentication instanceof OAuth2AuthenticationToken token) {
                registrationId = token.getAuthorizedClientRegistrationId();
            }

            logger.info("OAuth2 Provider: {}", registrationId);
            logger.info("OAuth2 User attributes: {}", oAuth2User.getAttributes());

            User user;
            switch (registrationId) {
                case "google" -> {
                    String googleId = oAuth2User.getAttributes().getOrDefault("sub", "").toString();
                    String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                    String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                    String pictureUrl = oAuth2User.getAttributes().getOrDefault("picture", "").toString();
                    String picture = convertGoogleImageToBase64(pictureUrl);
                    
                    logger.info("Google user - ID: {}, Email: {}, Name: {}", googleId, email, name);
                    
                    if (email == null || email.isEmpty()) {
                        throw new RuntimeException("Email is empty from Google OAuth2");
                    }
                    
                    // Split name into firstName and lastName
                    String[] nameParts = name.split(" ", 2);
                    String firstName = nameParts[0];
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";
                    
                    User finalNewUser = User.builder()
                            .email(email)
                            .firstName(firstName)
                            .lastName(lastName)
                            .image(picture)
                            .enable(true)
                            .provider(Provider.GOOGLE)
                            .providerId(googleId)
                            .build();

                    user = userRepository.findByEmail(email).orElseGet(() -> {
                        logger.info("Creating new user for email: {}", email);
                        User savedUser = userRepository.save(finalNewUser);
                        logger.info("User saved with ID: {}", savedUser.getId());
                        
                        // Assign GUEST role to new OAuth2 users
                        try {
                            logger.info("Attempting to assign GUEST role...");
                            Role guestRole = roleRepository.findByName(AppConstants.GUEST_ROLE)
                                    .orElseThrow(() -> new RuntimeException("GUEST role not found. Ensure roles are seeded in database at startup."));
                            logger.info("Found GUEST role: {}", guestRole.getName());
                            
                            // Ensure roles collection is initialized
                            if (savedUser.getRoles() == null) {
                                logger.warn("Roles collection was null, initializing...");
                                savedUser.setRoles(new java.util.HashSet<>());
                            }
                            
                            savedUser.getRoles().add(guestRole);
                            User userWithRole = userRepository.save(savedUser);
                            logger.info("GUEST role assigned to user: {}", email);
                            return userWithRole;
                        } catch (Exception e) {
                            logger.error("Failed to assign GUEST role to user: {}", email, e);
                            throw new RuntimeException("Role assignment failed: " + e.getMessage(), e);
                        }
                    });
                    logger.info("User authenticated/created: {}", user.getEmail());
                }

                default -> {
                    logger.error("Unsupported OAuth2 provider: {}", registrationId);
                    throw new RuntimeException("Unsupported OAuth2 provider: " + registrationId);
                }
            }

            // Generate tokens
            logger.info("Generating JWT tokens for user: {}", user.getEmail());
            String jti = UUID.randomUUID().toString();
            
            RefreshToken refreshTokenOb = RefreshToken.builder()
                    .jti(jti)
                    .user(user)
                    .revoked(false)
                    .createdAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                    .build();

            try {
                refreshTokenRepository.save(refreshTokenOb);
                logger.info("Refresh token saved with JTI: {}", jti);
            } catch (Exception e) {
                logger.error("Failed to save refresh token", e);
                throw new RuntimeException("Failed to save refresh token: " + e.getMessage(), e);
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());
            
            logger.info("Access token generated. Length: {}", accessToken.length());
            logger.info("Refresh token generated. Length: {}", refreshToken.length());
            
            try {
                cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
                logger.info("Refresh cookie attached to response");
            } catch (Exception e) {
                logger.error("Failed to attach refresh cookie", e);
                throw new RuntimeException("Failed to attach refresh cookie: " + e.getMessage(), e);
            }
            
            logger.info("Redirecting to success URL: {}", frontEndSuccessUrl);
            if (frontEndSuccessUrl == null || frontEndSuccessUrl.isEmpty()) {
                throw new RuntimeException("Frontend success redirect URL is not configured (app.auth.frontend.success-redirect)");
            }
            
            // Append access token as URL parameter for frontend to store
            String redirectUrl = frontEndSuccessUrl.contains("?") 
                ? frontEndSuccessUrl + "&token=" + accessToken
                : frontEndSuccessUrl + "?token=" + accessToken;
            
            response.sendRedirect(redirectUrl);
            logger.info("======= OAuth2 Authentication Completed Successfully =======");

        } catch (Exception e) {
            logger.error("======= OAuth2 Authentication Failed =======", e);
            logger.error("Error message: {}", e.getMessage());
            logger.error("Error cause: {}", e.getCause());
            
            try {
                response.setStatus(500);
                response.setContentType("application/json");
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
                String jsonError = "{\"message\":\"" + errorMsg.replace("\"", "\\\"") + "\",\"status\":\"INTERNAL_SERVER_ERROR\",\"statusCode\":500}";
                response.getWriter().write(jsonError);
                response.getWriter().flush();
                logger.info("Error response sent to client");
            } catch (Exception writeException) {
                logger.error("Failed to write error response", writeException);
            }
        }
    }


    private String convertGoogleImageToBase64(String pictureUrl) {
        if (pictureUrl == null || pictureUrl.isEmpty()) {
            logger.warn("No picture URL provided from Google");
            return "";
        }

        try {
            logger.info("Downloading Google profile image from: {}", pictureUrl);
            
            URL url = new URL(pictureUrl);
            byte[] imageBytes = url.openStream().readAllBytes();
            
            // Encode to Base64
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
            
            logger.info("Successfully converted Google image to Base64. Size: {} bytes", imageBytes.length);
            return base64Image;
            
        } catch (Exception e) {
            logger.warn("Failed to download/convert Google profile image: {}. Will use empty image.", e.getMessage());
            logger.debug("Stack trace:", e);
            return ""; 
        }
    }
}