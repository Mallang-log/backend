package com.mallang.auth.query.dao;

import com.mallang.auth.query.dao.support.MemberQuerySupport;
import com.mallang.auth.query.data.MemberProfileData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class MemberProfileDataDao {

    private final MemberQuerySupport memberQuerySupport;

    public MemberProfileData find(Long memberId) {
        return MemberProfileData.from(memberQuerySupport.getById(memberId));
    }
}
