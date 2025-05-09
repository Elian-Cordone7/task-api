package com.example.taskapi2.controller;

import com.example.taskapi2.model.User;
import com.example.taskapi2.util.JwtTokenUtil;
import com.example.taskapi2.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final String EMAIL_ALREADY_IN_USE = "Email already in use";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

        Optional<User> userOpt = userService.getUserByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }else {
            log.info("SE ENCONTRO USUARIO");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS);
        }

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);  // Retorna el token
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {

        Optional<User> existingUser = userService.getUserByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(EMAIL_ALREADY_IN_USE);
        }else {
            log.info("El usuario no se encuentra registrado, se permite registrarse.");
        }

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        User savedUser = userService.saveUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
