package com.mallang.comment.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.comment.query.repository.CommentQueryRepository;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.query.repository.PostQueryRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
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
    private final CommentDataPostProcessor commentDataPostProcessor;

    public List<CommentResponse> findAllByPost(
            Long postId,
            String blogName,
            @Nullable Long memberId,
            @Nullable String postPassword
    ) {
        Post post = postQueryRepository.getById(postId, blogName);
        Member member = memberQueryRepository.getMemberIfIdNotNull(memberId);
        post.validateAccess(member, postPassword);
        List<CommentResponse> result = commentQueryRepository.findAllByPost(postId, blogName)
                .stream()
                .filter(it -> Objects.isNull(it.getParent()))
                .map(CommentResponse::from)
                .toList();
        if (post.getWriter().equals(member)) {
            return result;
        }
        return commentDataPostProcessor.processSecret(result, memberId);
    }
}
