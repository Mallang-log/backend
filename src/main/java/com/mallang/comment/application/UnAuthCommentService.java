package com.mallang.comment.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthCommentCommand;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class UnAuthCommentService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentDeleteService commentDeleteService;

    public Long write(WriteUnAuthCommentCommand command) {
        Post post = postRepository.getById(command.postId(), command.blogName());
        Comment parent = commentRepository.getParentByIdAndPost(command.parentCommentId(), post);
        UnAuthComment comment = command.toCommand(post, parent);
        comment.write(command.postPassword());
        return commentRepository.save(comment).getId();
    }

    public void update(UpdateUnAuthCommentCommand command) {
        UnAuthComment comment = commentRepository.getUnAuthCommentById(command.commentId());
        comment.validateUpdate(command.password(), command.postPassword());
        comment.update(command.content());
    }

    public void delete(DeleteUnAuthCommentCommand command) {
        UnAuthComment comment = commentRepository.getUnAuthCommentById(command.commentId());
        Member member = memberRepository.getByIdIfIdNotNull(command.memberId());
        comment.validateDelete(member, command.password(), command.postPassword());
        comment.delete(commentDeleteService);
    }
}
