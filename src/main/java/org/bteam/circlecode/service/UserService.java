package org.bteam.circlecode.service;

import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.common.BusinessException;
import org.bteam.circlecode.config.BusinessErrorCode;
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
    private final AvatarService avatarService;

    public UserService(UserMapper userMapper, ObjectMapper objectMapper, AvatarService avatarService) {
       this.userMapper = userMapper;
       this.objectMapper = objectMapper;
       this.avatarService = avatarService;
    }

    public Map<String, String> userInfoService(Map<String, String> request){
        String username = request.get("username");
        if (username == null)
        {
            log.info("User info request failed: username is null");
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(),  BusinessErrorCode.INVALID_REQUEST.getMessage());
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
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(),  BusinessErrorCode.USER_NOT_FOUND.getMessage() );
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
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(),  BusinessErrorCode.USER_NOT_FOUND.getMessage() );
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
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(),  BusinessErrorCode.USER_NOT_FOUND.getMessage() );
        }

        userMapper.deleteByUsername(username);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User deleted successfully!");
        return data;
    }

    @Transactional
    public Map<String, String> userUpdateService(Map<String, String> request) {
        String username = request.get("username");
        if (username == null || username.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(), "Username is required");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.info("User update request failed: user {} not found", username);
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(), BusinessErrorCode.USER_NOT_FOUND.getMessage());
        }

        String nickname = request.get("nickname");
        String email = request.get("email");
        String phone = request.get("phone");

        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }

        userMapper.updateById(user);

        Map<String, String> data = new HashMap<>();
        data.put("message", "User updated successfully!");
        return data;
    }

    @Transactional
    public Map<String, String> uploadAvatarService(String username, org.springframework.web.multipart.MultipartFile file) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(), "Username is required");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.info("Avatar update request failed: user {} not found", username);
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND.getCode(), BusinessErrorCode.USER_NOT_FOUND.getMessage());
        }

        String oldAvatarUrl = user.getAvatar();
        String avatarUrl = avatarService.uploadAvatar(file, username);
        user.setAvatar(avatarUrl);
        userMapper.updateById(user);
        avatarService.deleteAvatarByUrl(oldAvatarUrl);

        Map<String, String> data = new HashMap<>();
        data.put("avatar", avatarUrl);
        return data;
    }


}
