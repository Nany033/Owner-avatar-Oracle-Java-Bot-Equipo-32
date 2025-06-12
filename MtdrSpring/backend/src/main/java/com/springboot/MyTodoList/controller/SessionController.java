package com.springboot.MyTodoList.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @GetMapping("/session")
    public ResponseEntity<?> checkSession() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(Map.of("status", "inactive"));
        }

        return ResponseEntity.ok(Map.of(
            "status", "active",
            "user", auth.getName()  
        ));
    }

    @GetMapping("/debug/cookies")
    public ResponseEntity<?> debugCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            logger.warn("❌ No cookies found in request.");
            return ResponseEntity.ok("No cookies found.");
        }

        for (Cookie cookie : cookies) {
            logger.info("🍪 Cookie: {} = {}", cookie.getName(), cookie.getValue());
        }

        return ResponseEntity.ok("Cookies logged to server.");
    }
}
