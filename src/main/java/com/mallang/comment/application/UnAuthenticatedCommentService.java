package com.mallang.comment.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.comment.application.command.DeleteUnAuthenticatedCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthenticatedComment;
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
public class UnAuthenticatedCommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentDeleteService commentDeleteService;

    public Long write(WriteUnAuthenticatedCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Comment parent = getParentCommentByIdAndPostId(command.parentCommentId(), command.postId());
        UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                .post(post)
                .content(command.content())
                .nickname(command.nickname())
                .parent(parent)
                .password(command.password())
                .build();
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
        UnAuthenticatedComment comment = commentRepository.getUnAuthenticatedCommentById(command.commentId());
        comment.update(command.password(), command.content(), command.postPassword());
    }

    public void delete(DeleteUnAuthenticatedCommentCommand command) {
        UnAuthenticatedComment comment = commentRepository.getUnAuthenticatedCommentById(command.commentId());
        Member member = (command.memberId() == null)
                ? null
                : memberRepository.getById(command.memberId());
        comment.delete(member, command.password(), commentDeleteService, command.postPassword());
    }
}
