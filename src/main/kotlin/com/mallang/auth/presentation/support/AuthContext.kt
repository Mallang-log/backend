package com.mallang.auth.presentation.support

import com.mallang.auth.exception.IncorrectUseAuthAtException
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope


@RequestScope
@Component
class AuthContext(
        private var memberId: Long? = null
) {

    fun unAuthenticated(): Boolean {
        return memberId == null
    }

    fun getMemberId(): Long {
        return memberId ?: throw IncorrectUseAuthAtException()
    }

    fun setMemberId(memberId: Long) {
        this.memberId = memberId
    }
}
