package com.mallang.auth.query.dao.model

import com.mallang.auth.domain.Member

data class MemberProfileQueryModel(
        val id: Long,
        val nickname: String,
        val profileImageUrl: String
) {
    companion object {
        fun from(member: Member): MemberProfileQueryModel =
                MemberProfileQueryModel(
                        member.id,
                        member.nickname,
                        member.profileImageUrl
                )
    }
}
