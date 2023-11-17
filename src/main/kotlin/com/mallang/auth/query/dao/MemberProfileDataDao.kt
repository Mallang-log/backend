package com.mallang.auth.query.dao

import com.mallang.auth.query.dao.model.MemberProfileQueryModel
import com.mallang.auth.query.dao.support.MemberQuerySupport
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class MemberProfileDataDao(
        private val memberQuerySupport: MemberQuerySupport
) {

    fun find(memberId: Long): MemberProfileQueryModel {
        return MemberProfileQueryModel.from(memberQuerySupport.getById(memberId))
    }
}
