package com.mallang.comment.domain.writer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UnAuthenticatedWriterRepository extends JpaRepository<CommentWriter, Long> {

}
