package com.mallang.comment.application;

import com.mallang.comment.application.command.DeleteCommentCommand;
import com.mallang.comment.application.command.WriteAuthenticatedCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthenticatedCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.AuthenticatedWriterRepository;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class CommentServiceTestHelper {

    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final AuthenticatedWriterRepository authenticatedWriterRepository;
    private final UnAuthenticatedWriterRepository unAuthenticatedWriterRepository;

    public Long 댓글을_작성한다(Long postId, String content, boolean secret, Long memberId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .build();
        return commentService.write(command);
    }

    public Long 비인증_댓글을_작성한다(Long postId, String content, String nickname, String password) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .build();
        return commentService.write(command);
    }

    public Long 대댓글을_작성한다(Long postId, String content, boolean secret, Long memberId, Long parentCommentId) {
        WriteAuthenticatedCommentCommand command = WriteAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .secret(secret)
                .memberId(memberId)
                .parentCommentId(parentCommentId)
                .build();
        return commentService.write(command);
    }

    public Long 비인증_대댓글을_작성한다(Long postId, String content, String nickname, String password, Long parentCommentId) {
        WriteUnAuthenticatedCommentCommand command = WriteUnAuthenticatedCommentCommand.builder()
                .postId(postId)
                .content(content)
                .nickname(nickname)
                .password(password)
                .parentCommentId(parentCommentId)
                .build();
        return commentService.write(command);
    }

    public Comment 댓글을_조회한다(Long 댓글_ID) {
        return commentRepository.getById(댓글_ID);
    }

    public Long 댓글의_작성자_ID를_반환한다(Long 댓글_ID) {
        return 댓글을_조회한다(댓글_ID).getCommentWriter().getId();
    }

    public CommentWriter 회원_ID로_인증된_댓글_작성자를_조회한다(Long 회원_ID) {
        return authenticatedWriterRepository.getByMemberId(회원_ID);
    }

    public CommentWriter ID로_비인증_댓글_작성자를_조회한다(Long 회원_ID) {
        return unAuthenticatedWriterRepository.findById(회원_ID).orElseThrow(EntityNotFoundException::new);
    }

    public void 댓글을_제거한다(Long 댓글_ID, Long 회원_ID) {
        DeleteCommentCommand command = DeleteCommentCommand.builder()
                .commentId(댓글_ID)
                .credential(new AuthenticatedWriterCredential(회원_ID))
                .build();
        commentService.delete(command);
    }

    public void 비인증_댓글을_제거한다(Long 댓글_ID, String 암호) {
        DeleteCommentCommand command = DeleteCommentCommand.builder()
                .commentId(댓글_ID)
                .credential(new UnAuthenticatedWriterCredential(암호))
                .build();
        commentService.delete(command);
    }
}
