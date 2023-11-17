package com.mallang.auth.query

import com.mallang.auth.query.dao.MemberProfileDataDao
import com.mallang.auth.query.dao.model.MemberProfileQueryModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@Service
class MemberQueryService(
        private val memberProfileDataDao: MemberProfileDataDao
) {

    fun findProfile(memberId: Long): MemberProfileQueryModel {
        return memberProfileDataDao.find(memberId)
    }
}
