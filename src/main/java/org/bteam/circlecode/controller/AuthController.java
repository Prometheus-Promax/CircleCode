package org.bteam.circlecode.controller;

import org.bteam.circlecode.common.Result;
import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginRequest) {
        Map<String, String> data =  authService.loginService(loginRequest);
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        Map<String, String> data = authService.registerService(user);
        return Result.success(data);
    }

    @GetMapping("/info")
    @ResponseBody
    public Result info(HttpRequest infoRequest) {
        User data = authService.infoService(infoRequest);
        return Result.success(data);
    }
}
