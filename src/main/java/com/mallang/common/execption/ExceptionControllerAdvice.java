package com.mallang.common.execption;

import static com.mallang.common.execption.ErrorCode.INTERNAL_SERVER_ERROR_CODE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MallangLogException.class)
    public ResponseEntity<ErrorCode> handleException(MallangLogException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.info("잘못될 요청 들어옴", e);
        return ResponseEntity.status(errorCode.status())
                .body(errorCode);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorCode> handleException(ServletRequestBindingException e) {
        ErrorCode errorCode = new ErrorCode(BAD_REQUEST, e.getMessage());
        log.info("잘못될 요청 들어옴", e);
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
