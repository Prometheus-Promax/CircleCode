package org.bteam.circlecode.service;

import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.mapper.UserMapper;
import org.bteam.circlecode.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
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
            log.info("Login attempt failed for non-existent user: {}", username);
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            log.info("Login attempt failed for user: {} due to incorrect password", username);
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getAccount_status() != 0)
        {
            log.info("Login attempt failed for user: {} due to disabled account", username);
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
        if (user.getUsername() == null || user.getPassword() == null ||
            user.getEmail() == null || user.getPhone() == null)
        {
            log.info("Registration attempt failed due to missing required fields");
            throw new RuntimeException("Missing required fields");
        }

        if (userMapper.selectByUsername(user.getUsername()) != null)
        {
            log.info("Registration attempt failed: username {} already exists", user.getUsername());
            throw new RuntimeException("User already exists");
        }

        if (userMapper.selectByEmail(user.getEmail()) != null)
        {
            log.info("Registration attempt failed: email {} already exists", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        if (userMapper.selectByPhone(user.getPhone()) != null)
        {
            log.info("Registration attempt failed: phone number {} already exists", user.getPhone());
            throw new RuntimeException("Phone number already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User registered successfully!");
        return data;
    }


}
