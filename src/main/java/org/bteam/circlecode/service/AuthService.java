package org.bteam.circlecode.service;

import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.mapper.UserMapper;
import org.bteam.circlecode.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserMapper userMapper,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> loginService(Map<String, String> loginRequest){
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userMapper.selectByUsername(username);
        if (user == null)
        {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getAccount_status() != 0)
        {
            throw new RuntimeException("User account is disabled");
        }

        String token = jwtUtil.generateToken(username);
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenHead", "Bearer ");
        return data;
    }

    @Transactional
    public Map<String, String> registerService(User user){

        if (userMapper.selectByUsername(user.getUsername()) != null)
        {
            throw new RuntimeException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User registered successfully!");
        return data;
    }


}
