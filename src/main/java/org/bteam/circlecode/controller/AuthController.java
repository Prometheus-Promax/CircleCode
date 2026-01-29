package org.bteam.circlecode.controller;

import org.bteam.circlecode.common.Response;
import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/CodeCircle/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        Map<String, String> data =  authService.loginService(loginRequest);
        Response<Object> response = Response.builder()
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, String> data = authService.registerService(user);
        Response<Object> response = Response.builder()
                .code(HttpStatus.OK.value())
                .message("Registration successful")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
