package com.mallang.auth.exception

import com.mallang.common.execption.ErrorCode
import com.mallang.common.execption.MallangLogException
import org.springframework.http.HttpStatus.UNAUTHORIZED

class NoAuthenticationSessionException : MallangLogException(
        ErrorCode(UNAUTHORIZED, "인증 정보가 없거나 만료되었습니다.")
)
