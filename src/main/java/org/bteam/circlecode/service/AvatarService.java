package org.bteam.circlecode.service;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.common.BusinessException;
import org.bteam.circlecode.config.BusinessErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@Service
public class AvatarService {
    private final ObsClient obsClient;

    @Value("${huawei.obs.bucket-name}")
    private String bucketName;
    @Value("${huawei.obs.domain-name}")
    private String domain;

    public AvatarService(ObsClient obsClient) {
        this.obsClient = obsClient;
    }

    public String uploadAvatar(MultipartFile file, String userName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(), "Avatar file is empty");
        }
        if (userName == null || userName.isBlank()) {
            throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(), "Username is required");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new BusinessException(BusinessErrorCode.INVALID_REQUEST.getCode(), "Invalid avatar file type");
            }

            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            String objectKey = "avatars/" + userName + "/" + fileName;

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream);
            obsClient.putObject(putObjectRequest);

            return "https://" + domain + "/" + objectKey;

        } catch (Exception e) {
            log.error("Failed to upload avatar to OBS", e);
            if (e instanceof BusinessException businessException) {
                throw businessException;
            }
            throw new BusinessException(BusinessErrorCode.FILE_UPLOAD_ERROR.getCode(), BusinessErrorCode.FILE_UPLOAD_ERROR.getMessage());
        }
    }

    public void deleteAvatarByUrl(String avatarUrl) {
        String objectKey = parseObjectKey(avatarUrl);
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }

        try {
            obsClient.deleteObject(bucketName, objectKey);
        } catch (Exception e) {
            // Avatar replacement should not fail if old file cleanup fails.
            log.warn("Failed to delete old avatar from OBS, objectKey={}", objectKey, e);
        }
    }

    private String parseObjectKey(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return null;
        }

        String normalizedDomain = domain == null ? "" : domain.trim().toLowerCase();
        String normalizedUrl = avatarUrl.trim();

        if (normalizedUrl.startsWith("avatars/")) {
            return normalizedUrl;
        }

        try {
            URI uri = new URI(normalizedUrl);
            String host = uri.getHost();
            if (host == null || !host.equalsIgnoreCase(normalizedDomain)) {
                return null;
            }
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                return null;
            }
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (URISyntaxException e) {
            log.warn("Invalid avatar url, skip deleting old avatar: {}", avatarUrl);
            return null;
        }
    }
}
