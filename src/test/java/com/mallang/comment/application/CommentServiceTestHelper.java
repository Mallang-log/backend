package com.mallang.comment.application;

import com.mallang.comment.application.command.DeleteAuthCommentCommand;
import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class CommentServiceTestHelper {

    private final CommentRepository commentRepository;
    private final AuthCommentService authCommentService;
    private final UnAuthCommentService unAuthCommentService;

    public Long 댓글을_작성한다(Long postId, String content, boolean secret, Long memberId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_댓글을_작성한다(Long postId, String content, String nickname, String password) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .build();
        return unAuthCommentService.write(command);
    }

    public Long 대댓글을_작성한다(Long postId, String content, boolean secret, Long memberId, Long parentCommentId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
        return authCommentService.write(command);
    }

    public Long 비인증_대댓글을_작성한다(Long postId, String content, String nickname, String password, Long parentCommentId) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
        return unAuthCommentService.write(command);
    }

    public AuthComment 인증된_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getAuthenticatedCommentById(댓글_ID);
    }

    public UnAuthComment 비인증_댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getUnAuthenticatedCommentById(댓글_ID);
    }

    public void 댓글을_제거한다(Long 댓글_ID, Long 회원_ID) {
        DeleteAuthCommentCommand command = DeleteAuthCommentCommand.builder()
                .commentId(댓글_ID)
                .memberId(회원_ID)
                .build();
        authCommentService.delete(command);
    }

    public void 비인증_댓글을_제거한다(Long 댓글_ID, String 암호) {
        DeleteUnAuthCommentCommand command = DeleteUnAuthCommentCommand.builder()
                .commentId(댓글_ID)
                .password(암호)
                .build();
        unAuthCommentService.delete(command);
    }
}
