package com.mallang.comment.application;

import com.mallang.comment.domain.writer.AuthenticatedWriterRepository;
import com.mallang.comment.domain.writer.CommentWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class CommentServiceTestHelper {

    private final AuthenticatedWriterRepository authenticatedWriterRepository;

    public CommentWriter 인증된_댓글_작성자를_조회한다(Long 회원_ID) {
        return authenticatedWriterRepository.getByMemberId(회원_ID);
    }
}
