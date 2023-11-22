package com.mallang.auth.query.dao;

import com.mallang.auth.query.response.MemberResponse;
import com.mallang.auth.query.support.MemberQuerySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class MemberDao {

    private final MemberQuerySupport memberQuerySupport;

    public MemberResponse find(Long memberId) {
        return MemberResponse.from(memberQuerySupport.getById(memberId));
    }
}
