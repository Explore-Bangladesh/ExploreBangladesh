package com.TeamDeadlock.ExploreBangladesh.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        try {
            String resetLink = "http://localhost:8080/reset-password.html?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Password Reset Request - Explore Bangladesh");
            helper.setText(buildResetEmailHtml(resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send reset email", e);
        }
    }

    private String buildResetEmailHtml(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }
                    .container {
                        background-color: white;
                        border-radius: 8px;
                        padding: 30px;
                        max-width: 600px;
                        margin: 0 auto;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #0096FF;
                        margin-bottom: 10px;
                    }
                    .content {
                        color: #333;
                        line-height: 1.6;
                    }
                    .button {
                        display: inline-block;
                        background-color: #0096FF;
                        color: white;
                        padding: 12px 30px;
                        border-radius: 5px;
                        text-decoration: none;
                        margin-top: 20px;
                        font-weight: 600;
                    }
                    .button:hover {
                        background-color: #0078d4;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #e0e0e0;
                        font-size: 12px;
                        color: #666;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 12px;
                        margin-top: 20px;
                        border-radius: 4px;
                        font-size: 13px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🌍 Explore Bangladesh</div>
                        <h2 style="color: #333; margin-bottom: 5px;">Password Reset Request</h2>
                    </div>

                    <div class="content">
                        <p>Hi there,</p>
                        <p>We received a request to reset your password. Click the button below to set a new password. This link is valid for the next 15 minutes.</p>

                        <center>
                            <a href=\"""" + resetLink + """
                            "\" class="button">Reset Password</a>
                        </center>

                        <p style="margin-top: 20px;">Or copy and paste this link in your browser:</p>
                        <p style="word-break: break-all; color: #0096FF; background-color: #f9f9f9; padding: 10px; border-radius: 4px;">""" + resetLink + """
                        </p>

                        <div class="warning">
                            ⚠️ If you didn't request a password reset, please ignore this email or contact support immediately. Your account is still secure.
                        </div>

                        <p style="margin-top: 20px;">Best regards,<br><strong>Explore Bangladesh Team</strong></p>
                    </div>

                    <div class="footer">
                        <p>This is an automated email. Please do not reply to this email.</p>
                        <p>© 2026 Explore Bangladesh. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
