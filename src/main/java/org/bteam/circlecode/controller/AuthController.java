package org.bteam.circlecode.controller;

import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        try{
            Map<String, String> data =  authService.loginService(loginRequest);
            return ResponseEntity.ok(data);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            Map<String, String> data = authService.registerService(user);
            return ResponseEntity.ok(data);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

}
