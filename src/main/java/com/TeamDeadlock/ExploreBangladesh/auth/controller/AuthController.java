package com.TeamDeadlock.ExploreBangladesh.auth.controller;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.RefreshToken;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.Role;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.User;
import com.TeamDeadlock.ExploreBangladesh.auth.payload.LoginRequest;
import com.TeamDeadlock.ExploreBangladesh.auth.payload.RefreshTokenRequest;
import com.TeamDeadlock.ExploreBangladesh.auth.payload.RoleDto;
import com.TeamDeadlock.ExploreBangladesh.auth.payload.TokenResponse;
import com.TeamDeadlock.ExploreBangladesh.auth.payload.UserDto;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.RefreshTokenRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.service.AuthService;
import com.TeamDeadlock.ExploreBangladesh.auth.service.impl.CookieService;
import com.TeamDeadlock.ExploreBangladesh.auth.service.impl.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ModelMapper mapper;
    private final CookieService cookieService;

    @Value("${security.jwt.token-type:Bearer}")
    private String tokenType;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        authenticate(loginRequest);

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isEnable()) {
            throw new DisabledException("User is disabled");
        }

        String jti = UUID.randomUUID().toString();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenEntity.getJti());

        cookieService.attachRefreshCookie(response, refreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);

        TokenResponse tokenResponse = new TokenResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTtlSeconds(),
                tokenType,
                toPayloadUser(user)
        );

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String refreshToken = readRefreshTokenFromRequest(body, request)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token type");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh token does not belong to this user");
        }

        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshTokenEntity);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenEntity.getJti());

        cookieService.attachRefreshCookie(response, newRefreshToken, (int) jwtService.getRefreshTtlSeconds());
        cookieService.addNoStoreHeaders(response);

        return ResponseEntity.ok(new TokenResponse(
                newAccessToken,
                newRefreshToken,
                jwtService.getAccessTtlSeconds(),
                tokenType,
                toPayloadUser(user)
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        readRefreshTokenFromRequest(null, request).ifPresent(token -> {
            try {
                if (jwtService.isRefreshToken(token)) {
                    String jti = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            } catch (JwtException ignored) {
                // Ignore malformed token during logout and continue clearing cookie.
            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping({"/register", "/signup"})
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto serviceUserDto = toServiceUser(userDto);
        com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto created = authService.registerUser(serviceUserDto);
        User persisted = userRepository.findByEmail(created.getEmail()).orElse(null);

        UserDto payload = persisted != null ? toPayloadUser(persisted) : toPayloadUser(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(payload);
    }

    private Authentication authenticate(LoginRequest loginRequest) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }

        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                    // Ignore and continue to empty result.
                }
            }
        }

        return Optional.empty();
    }

    private com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto toServiceUser(UserDto payloadUser) {
        com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto serviceUser =
                mapper.map(payloadUser, com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto.class);

        if (payloadUser.getName() != null && !payloadUser.getName().isBlank()) {
            String normalized = payloadUser.getName().trim();
            int firstSpace = normalized.indexOf(' ');
            if (firstSpace > 0) {
                serviceUser.setFirstName(normalized.substring(0, firstSpace));
                serviceUser.setLastName(normalized.substring(firstSpace + 1).trim());
            } else {
                serviceUser.setFirstName(normalized);
                serviceUser.setLastName(null);
            }
        }

        return serviceUser;
    }

    private UserDto toPayloadUser(User user) {
        String name = ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();
        Set<RoleDto> roleDtos = user.getRoles() == null
                ? Set.of()
                : user.getRoles().stream()
                .map(this::toRoleDto)
                .collect(Collectors.toSet());

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(name.isBlank() ? null : name)
                .image(user.getImage())
                .enable(user.isEnable())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .provider(user.getProvider())
                .roles(roleDtos)
                .build();
    }

    private UserDto toPayloadUser(com.TeamDeadlock.ExploreBangladesh.auth.dto.UserDto user) {
        String name = ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(name.isBlank() ? null : name)
                .image(user.getImage())
                .enable(Boolean.TRUE.equals(user.getEnable()))
                .provider(user.getProvider())
                .build();
    }

    private RoleDto toRoleDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
