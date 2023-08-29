package com.mallang.comment.application;

import com.mallang.auth.domain.event.MemberSignUpEvent;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.AuthenticatedWriterRepository;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentEventHandler {

    private final MemberRepository memberRepository;
    private final AuthenticatedWriterRepository authenticatedWriterRepository;

    @EventListener(MemberSignUpEvent.class)
    void deleteCategoryFromPost(MemberSignUpEvent event) {
        Member member = memberRepository.getById(event.memberId());
        AuthenticatedWriter writer = new AuthenticatedWriter(member);
        authenticatedWriterRepository.save(writer);
    }
}
