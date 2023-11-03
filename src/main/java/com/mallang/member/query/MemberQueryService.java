package com.mallang.member.query;

import com.mallang.member.query.dao.MemberProfileDataDao;
import com.mallang.member.query.data.MemberProfileData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberProfileDataDao memberProfileDataDao;

    public MemberProfileData findProfile(Long memberId) {
        return memberProfileDataDao.find(memberId);
    }
}
