package com.mallang.reference.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.application.command.UpdateReferenceLinkCommand;
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
        ReferenceLink referenceLink = command.toReferenceLink(member, blog);
        return referenceLinkRepository.save(referenceLink).getId();
    }

    public void update(UpdateReferenceLinkCommand command) {
        Member member = memberRepository.getById(command.memberId());
        ReferenceLink link = referenceLinkRepository.getById(command.referenceLinkId());
        link.validateMember(member);
        link.update(command.url(), command.title(), command.memo());
    }

    public void delete(Long referenceLinkId, Long memberId) {
        Member member = memberRepository.getById(memberId);
        ReferenceLink link = referenceLinkRepository.getById(referenceLinkId);
        link.validateMember(member);
        referenceLinkRepository.delete(link);
    }
}
