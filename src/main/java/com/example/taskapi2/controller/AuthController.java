package com.example.taskapi2.controller;

import com.example.taskapi2.model.User;
import com.example.taskapi2.service.ResponseService;
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

    private static final String INVALID_CREDENTIALS = "Credenciales invalidas";
    private static final String EMAIL_ALREADY_IN_USE = "El email ya se encuentra registrado";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ResponseService responseService;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserService userService, BCryptPasswordEncoder passwordEncoder, ResponseService responseService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.responseService = responseService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

        Optional<User> userOpt = userService.getUserByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return responseService.unauthorizedErrorResponse(INVALID_CREDENTIALS);
        }else {
            log.info("SE ENCONTRO USUARIO");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return responseService.unauthorizedErrorResponse(INVALID_CREDENTIALS);
        }

        String token = jwtTokenUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {

        Optional<User> existingUser = userService.getUserByEmail(newUser.getEmail());
        if (existingUser.isPresent()) {
            return responseService.errorResponse(EMAIL_ALREADY_IN_USE,null);

        }else {
            log.info("El usuario no se encuentra registrado, se permite registrarse.");
        }

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        User savedUser = userService.saveUser(newUser);
        return responseService.createdResponse(null,ResponseEntity.status(HttpStatus.CREATED).body(savedUser));
    }
}
