package org.bteam.circlecode.service;

import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.common.BusinessException;
import org.bteam.circlecode.config.BusinessErrorCode;
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
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil,
                          TokenService tokenService,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
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
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(),  BusinessErrorCode.USER_NOT_FOUND.getMessage());
        }

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            log.info("Login attempt failed for user: {} due to incorrect password", username);
            throw new BusinessException(BusinessErrorCode.INVALID_CREDENTIALS.getCode(),  BusinessErrorCode.INVALID_CREDENTIALS.getMessage());
        }

        if (user.getAccount_status() != 0)
        {
            log.info("Login attempt failed for user: {} due to disabled account", username);
            throw new BusinessException(BusinessErrorCode.ACCOUNT_DISABLED.getCode(),  BusinessErrorCode.ACCOUNT_DISABLED.getMessage());
        }

        String token = jwtUtil.generateToken(username, String.valueOf(user.getId()));
        tokenService.addToken(token, jwtUtil.getExpiration(), String.valueOf(user.getId()));
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("tokenHead", "Bearer ");
        return data;
    }

    public Map<String, String> logoutService(Map<String, String> logoutRequest){
        String token = logoutRequest.get("token");
        String userId = jwtUtil.getUserIdFromToken(token);
        tokenService.removeToken(userId);
        Map<String, String> data = new HashMap<>();
        data.put("message", "User logout successfully!");
        return data;
    }

    @Transactional
    public Map<String, String> registerService(User user){
        if (user.getUsername() == null || user.getPassword() == null ||
            user.getEmail() == null || user.getPhone() == null)
        {
            log.info("Registration attempt failed due to missing required fields");
            throw new BusinessException(BusinessErrorCode.OPERATION_FAILED.getCode(),  "Missing required fields");
        }

        if (userMapper.selectByUsername(user.getUsername()) != null)
        {
            log.info("Registration attempt failed: username {} already exists", user.getUsername());
            throw new BusinessException(BusinessErrorCode.USER_ALREADY_EXISTS.getCode(),  BusinessErrorCode.USER_ALREADY_EXISTS.getMessage());
        }

        if (userMapper.selectByEmail(user.getEmail()) != null)
        {
            log.info("Registration attempt failed: email {} already exists", user.getEmail());
            throw new BusinessException(BusinessErrorCode.EMAIL_ALREADY_EXISTS.getCode(),  BusinessErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        if (userMapper.selectByPhone(user.getPhone()) != null)
        {
            log.info("Registration attempt failed: phone number {} already exists", user.getPhone());
            throw new BusinessException(BusinessErrorCode.PHONE_ALREADY_EXISTS.getCode(),  BusinessErrorCode.PHONE_ALREADY_EXISTS.getMessage());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User registered successfully!");
        return data;
    }


}
