package com.mallang.comment.application;

import com.mallang.comment.application.command.WriteAnonymousCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AnonymousCommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long write(WriteAnonymousCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Comment comment = command.toComment(post);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }
}
