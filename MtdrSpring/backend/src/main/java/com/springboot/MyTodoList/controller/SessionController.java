package com.springboot.MyTodoList.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpSession session, Principal principal) {
        if (principal != null) {
            logger.info("🧭 Session check: VALID sessionId={}, user={}", session.getId(), principal.getName());
            return ResponseEntity.ok().body(Map.of(
                    "status", "active",
                    "user", principal.getName()));
        } else {
            logger.warn("⚠️ Session check: INVALID or missing principal (sessionId={})",
                    session != null ? session.getId() : "none");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "inactive"));
        }
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
