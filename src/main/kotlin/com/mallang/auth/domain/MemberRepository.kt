package com.mallang.auth.domain

import com.mallang.auth.exception.NotFoundMemberException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {

    override fun getById(id: Long) = findByIdOrNull(id)
            ?: throw NotFoundMemberException()

    fun findByOauthId(oauthId: OauthId): Optional<Member>
}
