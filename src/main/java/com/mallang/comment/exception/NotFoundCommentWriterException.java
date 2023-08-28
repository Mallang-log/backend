package com.mallang.comment.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class NotFoundCommentWriterException extends MallangLogException {

    public NotFoundCommentWriterException() {
        super(new ErrorCode(
                INTERNAL_SERVER_ERROR,
                "해당 회원으로 등록된 인증된 댓글 사용자 정보가 없습니다. (서버 로직 오류)")
        );
    }
}
