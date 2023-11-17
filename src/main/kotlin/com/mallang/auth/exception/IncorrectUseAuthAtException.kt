package com.mallang.auth.exception

import com.mallang.common.execption.ErrorCode
import com.mallang.common.execption.MallangLogException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class IncorrectUseAuthAtException : MallangLogException(
        ErrorCode(INTERNAL_SERVER_ERROR, "@Auth 어노테이션을 잘못 사용했습니다.")
)
