package com.mallang.auth.exception

import com.mallang.common.execption.ErrorCode
import com.mallang.common.execption.MallangLogException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

class UnsupportedOauthTypeException : MallangLogException(
        ErrorCode(INTERNAL_SERVER_ERROR, "Oauth 에 문제가 있습니다.")
)
