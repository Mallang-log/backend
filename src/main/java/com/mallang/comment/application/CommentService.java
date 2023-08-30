package com.mallang.comment.application;

import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.application.command.UpdateCommentCommand;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.AuthenticatedWriterRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthenticatedWriterRepository authenticatedWriterRepository;
    private final CommentDeleteService commentDeleteService;

    public Long write(WriteAuthenticatedCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        AuthenticatedWriter writer = authenticatedWriterRepository.getByMemberId(command.memberId());
        Comment parent = getParentComment(command.parentCommentId());
        Comment comment = command.toComment(post, writer, parent);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    private Comment getParentComment(@Nullable Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }
        return commentRepository.getById(parentCommentId);
    }

    public Long write(WriteUnAuthenticatedCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Comment parent = getParentComment(command.parentCommentId());
        Comment comment = command.toComment(post, parent);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    public void update(UpdateCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        comment.update(command.credential(), command.content(), command.secret());
    }

    public void delete(DeleteCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        commentDeleteService.delete(comment, command.credential());
    }
}
