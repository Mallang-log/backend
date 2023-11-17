package com.mallang.auth.exception

import com.mallang.common.execption.ErrorCode
import com.mallang.common.execption.MallangLogException
import org.springframework.http.HttpStatus.NOT_FOUND

class NotFoundMemberException : MallangLogException(
        ErrorCode(NOT_FOUND, "존재하지 않는 회원입니다.")
)
