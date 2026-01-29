package org.bteam.circlecode.service;

import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.entity.User;
import org.bteam.circlecode.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public UserService(UserMapper userMapper, ObjectMapper objectMapper) {
       this.userMapper = userMapper;
       this.objectMapper = objectMapper;
    }

    public Map<String, String> userInfoService(Map<String, String> request){
        String username = request.get("username");
        if (username == null)
        {
            log.info("User info request failed: username is null");
            throw new RuntimeException("User not found");
        }

        User user = userMapper.selectByUsername(username);
        user.setPassword(null);

        return objectMapper.convertValue(user, Map.class);
    }

    @Transactional
    public Map<String, String> userDisableService(Map<String, String> request) {
        String username = request.get("username");
        User user = userMapper.selectByUsername(username);
        if (user == null)
        {
            log.info("User disable request failed: user {} not found", username);
            throw new RuntimeException("User not found");
        }

        user.setRecord_status(0);
        userMapper.updateById(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User disable successfully!");
        return data;
    }

    @Transactional
    public Map<String, String> userEnableService(Map<String, String> deleteRequest) {
        String username = deleteRequest.get("username");
        User user = userMapper.selectByUsername(username);

        if (user == null)
        {
            log.info("User enable request failed: user {} not found", username);
            throw new RuntimeException("User not found");
        }

        user.setRecord_status(1);
        userMapper.updateById(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User enable successfully!");
        return data;
    }

    @Transactional
    public Map<String, String> userDeleteService(Map<String, String> deleteRequest) {
        String username = deleteRequest.get("username");
        User user = userMapper.selectByUsername(username);
        if (user == null)
        {
            log.info("User delete request failed: user {} not found", username);
            throw new RuntimeException("User not found");
        }

        userMapper.deleteByUsername(username);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User deleted successfully!");
        return data;
    }


}
