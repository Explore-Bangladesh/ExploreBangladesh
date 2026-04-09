package com.TeamDeadlock.ExploreBangladesh.auth.controller;

import com.TeamDeadlock.ExploreBangladesh.auth.entity.PasswordResetToken;
import com.TeamDeadlock.ExploreBangladesh.auth.entity.User;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.PasswordResetTokenRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.repository.UserRepository;
import com.TeamDeadlock.ExploreBangladesh.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ForgotPasswordController {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint to request password reset
     * POST /api/v1/auth/forgot-password?email=user@example.com
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();

        try {
            // 1. Check if user exists
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                // For security, return success even if email doesn't exist
                response.put("message", "If the email exists, a password reset link has been sent");
                return ResponseEntity.ok(response);
            }

            // 2. Invalidate any previous tokens for this email
            passwordResetTokenRepository.findByEmailAndUsedFalse(email).ifPresent(token -> {
                token.setUsed(true);
                passwordResetTokenRepository.save(token);
            });

            // 3. Generate unique token
            String token = UUID.randomUUID().toString();

            // 4. Save token with 15-minute expiry
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .email(email)
                    .expiryDate(LocalDateTime.now().plusMinutes(15))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(resetToken);

            // 5. Send reset email
            emailService.sendPasswordResetEmail(email, token);

            response.put("message", "Password reset link has been sent to your email");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in forgot password request", e);
            response.put("error", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint to verify reset token
     * GET /api/v1/auth/verify-reset-token?token=xxx
     */
    @GetMapping("/verify-reset-token")
    public ResponseEntity<Map<String, Object>> verifyResetToken(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);

            if (resetTokenOptional.isEmpty()) {
                response.put("valid", false);
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            PasswordResetToken resetToken = resetTokenOptional.get();

            // Check if token is expired
            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                response.put("valid", false);
                response.put("message", "Token has expired. Please request a new password reset");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Check if token is already used
            if (resetToken.isUsed()) {
                response.put("valid", false);
                response.put("message", "This token has already been used");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.put("valid", true);
            response.put("message", "Token is valid");
            response.put("email", resetToken.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error verifying reset token", e);
            response.put("valid", false);
            response.put("error", "An error occurred while verifying the token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint to reset password
     * POST /api/v1/auth/reset-password
     * Body: { "token": "xxx", "newPassword": "password123" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        Map<String, String> response = new HashMap<>();

        try {
            // 1. Validate input
            if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6) {
                response.put("error", "Password must be at least 6 characters long");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 2. Verify token
            Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findByToken(token);

            if (resetTokenOptional.isEmpty()) {
                response.put("error", "Invalid token");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            PasswordResetToken resetToken = resetTokenOptional.get();

            // 3. Check if token is expired
            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                response.put("error", "Token has expired");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 4. Check if token is already used
            if (resetToken.isUsed()) {
                response.put("error", "This token has already been used");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 5. Get user and update password
            Optional<User> userOptional = userRepository.findByEmail(resetToken.getEmail());
            if (userOptional.isEmpty()) {
                response.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // 6. Mark token as used
            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);

            log.info("Password reset successfully for user: {}", resetToken.getEmail());
            response.put("message", "Password reset successfully. Please login with your new password");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error resetting password", e);
            response.put("error", "An error occurred while resetting the password");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
