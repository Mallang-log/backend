package com.mallang.comment.application;

import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.application.command.UpdateCommentCommand;
import com.mallang.comment.application.command.WriteAnonymousCommentCommand;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.AuthenticatedWriterRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
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

    public Long write(WriteAuthenticatedCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        AuthenticatedWriter writer = authenticatedWriterRepository.getByMemberId(command.memberId());
        Comment comment = command.toComment(post, writer);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    public Long write(WriteAnonymousCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Comment comment = command.toComment(post);
        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    public void update(UpdateCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        comment.update(command.credential(), command.content(), command.secret());
    }

    public void delete(DeleteCommentCommand command) {
        Comment comment = commentRepository.getById(command.commentId());
        comment.delete(command.credential());
        commentRepository.delete(comment);
    }
}
