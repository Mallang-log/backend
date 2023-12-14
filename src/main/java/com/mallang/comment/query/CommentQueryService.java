package com.mallang.comment.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.UnAuthComment;
import com.mallang.comment.query.repository.CommentQueryRepository;
import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.query.repository.PostQueryRepository;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentQueryService {

    private final PostQueryRepository postQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final CommentQueryRepository commentQueryRepository;

    public List<CommentResponse> findAllByPost(
            Long postId,
            String blogName,
            @Nullable Long memberId,
            @Nullable String postPassword
    ) {
        Post post = postQueryRepository.getById(postId, blogName);
        Member member = memberQueryRepository.getMemberIfIdNotNull(memberId);
        post.validateAccess(member, postPassword);
        List<Comment> parents = commentQueryRepository.findAllByPost(postId, blogName)
                .stream()
                .filter(it -> it.getParent() == null)
                .toList();
        List<CommentResponse> responses = new ArrayList<>();
        for (Comment parent : parents) {
            List<Comment> children = parent.getChildren();
            CommentResponse parentResponse = mapToResponse(parent, member);
            parentResponse.setChildren(mapToResponses(children, member));
            responses.add(parentResponse);
        }
        return responses;
    }

    private List<CommentResponse> mapToResponses(List<Comment> comments, Member member) {
        return comments.stream()
                .map(it -> mapToResponse(it, member))
                .toList();
    }

    private CommentResponse mapToResponse(Comment comment, Member member) {
        if (comment instanceof AuthComment authComment) {
            if (authComment.canSee(member)) {
                return AuthCommentResponse.from(authComment);
            }
            return AuthCommentResponse.protectFrom(authComment);
        }
        return UnAuthCommentResponse.from((UnAuthComment) comment);
    }
}
