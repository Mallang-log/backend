package com.mallang.comment.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.comment.application.command.DeleteAuthCommentCommand;
import com.mallang.comment.application.command.UpdateAuthCommentCommand;
import com.mallang.comment.application.command.WriteAuthCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthCommentService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentDeleteService commentDeleteService;

    public Long write(WriteAuthCommentCommand command) {
        Post post = postRepository.getById(command.postId());
        Member writer = memberRepository.getById(command.memberId());
        Comment parent = commentRepository.getParentByIdAndPostId(command.parentCommentId(), command.postId());
        AuthComment comment = command.toComment(post, writer, parent);
        comment.write(command.postPassword());
        return commentRepository.save(comment).getId();
    }

    public void update(UpdateAuthCommentCommand command) {
        AuthComment comment = commentRepository.getAuthCommentById(command.commentId());
        Member writer = memberRepository.getById(command.memberId());
        comment.update(writer, command.content(), command.secret(), command.postPassword());
    }

    public void delete(DeleteAuthCommentCommand command) {
        AuthComment comment = commentRepository.getAuthCommentById(command.commentId());
        Member member = memberRepository.getById(command.memberId());
        comment.delete(member, commentDeleteService, command.postPassword());
    }
}
