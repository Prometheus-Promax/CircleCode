package org.bteam.circlecode.handler;

import lombok.extern.slf4j.Slf4j;
import org.bteam.circlecode.common.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GloableExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> HandleBusinessException(Exception e) {
        log.error("Exception: ", e);
        Response response = Response.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> HandleRuntimeException(RuntimeException e) {
        log.error("Exception: ", e);
        Response response = Response.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
