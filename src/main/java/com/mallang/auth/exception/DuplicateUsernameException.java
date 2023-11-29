package com.mallang.auth.exception;

import static org.springframework.http.HttpStatus.CONFLICT;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class DuplicateUsernameException extends MallangLogException {

    public DuplicateUsernameException() {
        super(new ErrorCode(CONFLICT, "이미 존재하는 아이디입니다. 다른 아이디로 가입해주세요."));
    }
}
