package com.springboot.MyTodoList.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;

@RestController
@RequestMapping("/api/signup")  // API prefix to match other routes
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        logger.info("Received signup request for userId: {}", user.getUserId());

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRol("developer"); // You can customize or move this logic

            userService.saveUser(user);

            logger.info("User {} successfully registered.", user.getUserId());
            return new ResponseEntity<>(HttpStatus.CREATED); // or return the saved user if needed
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage());
            return new ResponseEntity<>("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
