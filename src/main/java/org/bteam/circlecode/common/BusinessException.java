package org.bteam.circlecode.common;


import lombok.Getter;

public class BusinessException extends RuntimeException {
    @Getter
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}