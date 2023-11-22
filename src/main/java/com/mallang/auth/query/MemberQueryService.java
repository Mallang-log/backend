package com.mallang.auth.query;

import com.mallang.auth.query.dao.MemberDao;
import com.mallang.auth.query.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberDao memberDao;

    public MemberResponse findProfile(Long memberId) {
        return memberDao.find(memberId);
    }
}
