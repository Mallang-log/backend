package com.mallang.comment.application;

import com.mallang.auth.domain.event.MemberSignUpEvent;
import com.mallang.comment.domain.AuthenticatedWriter;
import com.mallang.comment.domain.AuthenticatedWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentEventHandler {

    private final AuthenticatedWriterRepository authenticatedWriterRepository;

    @EventListener(MemberSignUpEvent.class)
    void deleteCategoryFromPost(MemberSignUpEvent event) {
        AuthenticatedWriter writer = new AuthenticatedWriter(event.memberId());
        authenticatedWriterRepository.save(writer);
    }
}
