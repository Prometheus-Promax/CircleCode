package org.bteam.circlecode.config;

import lombok.Getter;

public enum BusinessErrorCode {
    USER_NOT_FOUND(1001, "User not found"),
    INVALID_CREDENTIALS(1002, "Invalid credentials"),
    USER_ALREADY_EXISTS(1003, "User already exists"),
    EMAIL_ALREADY_EXISTS(1004, "Email already exists"),
    PHONE_ALREADY_EXISTS(1005, "Phone number already exists"),
    WEAK_PASSWORD(1006, "Password does not meet security requirements"),
    UNAUTHORIZED_ACCESS(1007, "Unauthorized access"),
    RESOURCE_NOT_FOUND(1008, "Requested resource not found"),
    OPERATION_FAILED(1009, "Operation failed due to server error"),
    ACCOUNT_DISABLED(1010, "Account is disabled"),
    INVALID_REQUEST(1011, "Invalid request parameters"),

    FILE_READ_ERROR(2001, "Failed to read file"),
    FILE_UPLOAD_ERROR(2002, "Failed to upload file"),
    FILE_DELETE_ERROR(2003, "Failed to delete file");

    @Getter
    private final int code;
    @Getter
    private final String message;

    BusinessErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
