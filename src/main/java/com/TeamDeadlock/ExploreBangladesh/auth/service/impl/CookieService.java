package com.TeamDeadlock.ExploreBangladesh.auth.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    @Value("${security.jwt.refresh-cookie-name:REFRESH_TOKEN}")
    private String refreshTokenCookieName;

    @Value("${security.jwt.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${security.jwt.refresh-cookie-http-only:true}")
    private boolean refreshCookieHttpOnly;

    @Value("${security.jwt.refresh-cookie-same-site:Lax}")
    private String refreshCookieSameSite;

    @Value("${security.jwt.refresh-cookie-path:/}")
    private String refreshCookiePath;

    public String getRefreshTokenCookieName() {
        return refreshTokenCookieName;
    }

    public void attachRefreshCookie(HttpServletResponse response, String refreshToken, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .httpOnly(refreshCookieHttpOnly)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenCookieName, "")
                .httpOnly(refreshCookieHttpOnly)
                .secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite)
                .path(refreshCookiePath)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void addNoStoreHeaders(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
    }
}
