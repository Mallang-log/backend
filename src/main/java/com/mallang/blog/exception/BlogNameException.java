package com.mallang.blog.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.mallang.common.execption.ErrorCode;
import com.mallang.common.execption.MallangLogException;

public class BlogNameException extends MallangLogException {

    public BlogNameException() {
        super(new ErrorCode(BAD_REQUEST,
                """
                        블로그 이름은 다음 조건을 만족해야 합니다.
                        - 최소 4자 최대 32자 이내여야 한다
                        - 영문 소문자 숫자 하이픈으로만 구성되어야 한다
                        - 하이폰은 연속해서 사용할 수 없다
                        - 하이폰으로 시작하거나 끝나서는 안된다
                        """
        ));
    }
}
