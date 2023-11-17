package com.mallang.auth.query.dao.support

import com.mallang.auth.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull


interface MemberQuerySupport : JpaRepository<Member, Long?> {
    fun getById(id: Long) = findByIdOrNull(id)
            ?: throw NoSuchElementException("id가 ${id}인 회원을 찾을 수 없습니다.")
}
