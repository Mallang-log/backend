package com.mallang.blog.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.blog.query.response.BlogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogQueryService {

    private final BlogQueryRepository blogQueryRepository;
    private final MemberQueryRepository memberQueryRepository;

    public BlogResponse findByName(String blogName) {
        return BlogResponse.from(blogQueryRepository.getWithOwnerByName(blogName));
    }

    public BlogResponse findByOwnerId(Long memberId) {
        Member member = memberQueryRepository.getById(memberId);
        return BlogResponse.from(blogQueryRepository.getByOwner(member));
    }
}
