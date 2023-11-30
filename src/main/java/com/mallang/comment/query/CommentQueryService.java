package com.mallang.comment.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.comment.query.repository.CommentQueryRepository;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
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

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentQueryRepository commentQueryRepository;
    private final CommentDataPostProcessor commentDataPostProcessor;

    public List<CommentResponse> findAllByPost(Long postId,
                                               String blogName,
                                               @Nullable Long memberId,
                                               @Nullable String postPassword) {
        Post post = postRepository.getById(postId, blogName);
        Member member = findMember(memberId);
        post.validatePostAccessibility(member, postPassword);
        List<CommentResponse> result = commentQueryRepository.findAllByPost(postId, blogName)
                .stream()
                .filter(it -> Objects.isNull(it.getParent()))
                .map(CommentResponse::from)
                .toList();
        if (post.isWriter(member)) {
            return result;
        }
        return commentDataPostProcessor.processSecret(result, memberId);
    }

    private Member findMember(Long memberId) {
        return (memberId == null) ? null : memberRepository.getById(memberId);
    }
}
