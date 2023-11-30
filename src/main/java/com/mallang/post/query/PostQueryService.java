package com.mallang.post.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostLikeQueryRepository;
import com.mallang.post.query.repository.PostQueryRepository;
import com.mallang.post.query.repository.PostSearchDao.PostSearchCond;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryService {

    private final MemberRepository memberRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostLikeQueryRepository postLikeQueryRepository;

    public PostDetailResponse getByIdAndBlogName(
            Long id,
            String blogName,
            @Nullable Long memberId,
            @Nullable String postPassword
    ) {
        Member member = findMember(memberId);
        Post post = postQueryRepository.getById(id, blogName);
        try {
            post.validatePostAccessibility(member, postPassword);
            if (memberId == null) {
                return PostDetailResponse.from(post);
            }
            boolean isLiked = postLikeQueryRepository.existsByMemberIdAndPostId(memberId, id, blogName);
            return PostDetailResponse.withLiked(post, isLiked);
        } catch (NoAuthorityPostException e) {
            if (post.getVisibilityPolish().getVisibility() == Visibility.PRIVATE) {
                throw e;
            }
            return PostDetailResponse.protectedPost(post);
        }
    }

    public Page<PostSearchResponse> search(
            PostSearchCond cond,
            Pageable pageable,
            @Nullable Long memberId
    ) {
        Member member = findMember(memberId);
        return postQueryRepository.search(memberId, cond, pageable)
                .map(post -> {
                    try {
                        post.validatePostAccessibility(member, null);
                        return PostSearchResponse.from(post);
                    } catch (NoAuthorityPostException e) {
                        return PostSearchResponse.protectedPost(post);
                    }
                });
    }

    @Nullable
    private Member findMember(@Nullable Long memberId) {
        if (memberId == null) {
            return null;
        }
        return memberRepository.getById(memberId);
    }
}

