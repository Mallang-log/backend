package com.mallang.auth.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.auth.query.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public MemberResponse findProfile(Long memberId) {
        Member member = memberQueryRepository.getById(memberId);
        return MemberResponse.from(member);
    }
}
