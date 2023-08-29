package com.mallang.comment.domain.writer;

import com.mallang.comment.exception.NotFoundCommentWriterException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticatedWriterRepository extends JpaRepository<AuthenticatedWriter, Long> {

    default AuthenticatedWriter getByMemberId(Long memberId) {
        return findByMemberId(memberId)
                .orElseThrow(NotFoundCommentWriterException::new);
    }

    Optional<AuthenticatedWriter> findByMemberId(Long memberId);
}
