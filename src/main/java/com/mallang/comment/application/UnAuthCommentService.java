package com.mallang.comment.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UnAuthCommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentDeleteService commentDeleteService;

    public Long write(WriteUnAuthenticatedCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Comment parent = getParentCommentByIdAndPostId(command.parentCommentId(), command.postId());
        UnAuthComment comment = command.toCommand(post, parent);
        comment.write(command.postPassword());
        return commentRepository.save(comment).getId();
    }

    private Comment getParentCommentByIdAndPostId(@Nullable Long parentCommentId, Long postId) {
        if (parentCommentId == null) {
            return null;
        }
        return commentRepository.getByIdAndPostId(parentCommentId, postId);
    }

    public void update(UpdateUnAuthenticatedCommentCommand command) {
        UnAuthComment comment = commentRepository.getUnAuthenticatedCommentById(command.commentId());
        comment.update(command.password(), command.content(), command.postPassword());
    }

    public void delete(DeleteUnAuthCommentCommand command) {
        UnAuthComment comment = commentRepository.getUnAuthenticatedCommentById(command.commentId());
        Member member = (command.memberId() == null)
                ? null
                : memberRepository.getById(command.memberId());
        comment.delete(member, command.password(), commentDeleteService, command.postPassword());
    }
}