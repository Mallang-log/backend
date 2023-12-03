package com.mallang.reference.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.domain.ReferenceLink;
import com.mallang.reference.domain.ReferenceLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReferenceLinkService {

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final ReferenceLinkRepository referenceLinkRepository;

    public Long save(SaveReferenceLinkCommand command) {
        Blog blog = blogRepository.getByName(command.blogName());
        Member member = memberRepository.getById(command.memberId());
        blog.validateOwner(member);
        ReferenceLink referenceLink = command.toReferenceLink(blog);
        return referenceLinkRepository.save(referenceLink).getId();
    }
}
