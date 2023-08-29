package com.mallang.common.execption;

import static com.mallang.common.execption.ErrorCode.INTERNAL_SERVER_ERROR_CODE;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MallangLogException.class)
    public ResponseEntity<ErrorCode> handleException(MallangLogException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.info("잘못될 요청 들어옴: {}", e.getMessage(), e);
        return ResponseEntity.status(errorCode.status())
                .body(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorCode> handleException(Exception e) {
        log.error("예상치 못한 예외 발생: ", e);
        return ResponseEntity.internalServerError()
                .body(INTERNAL_SERVER_ERROR_CODE);
    }
}
