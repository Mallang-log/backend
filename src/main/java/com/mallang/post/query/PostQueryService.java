package com.mallang.post.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
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

    private final PostQueryRepository postQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final PostLikeQueryRepository postLikeQueryRepository;

    public PostDetailResponse getByIdAndBlogName(
            Long postId,
            String blogName,
            @Nullable Long memberId,
            @Nullable String postPassword
    ) {
        Member member = memberQueryRepository.getMemberIfIdNotNull(memberId);
        Post post = postQueryRepository.getById(postId, blogName);
        try {
            post.validateAccess(member, postPassword);
            return PostDetailResponse.withLiked(post, isLiked(post, member));
        } catch (NoAuthorityPostException e) {
            if (post.getVisibility() == Visibility.PRIVATE) {
                throw e;
            }
            return PostDetailResponse.protectedPost(post);
        }
    }

    private boolean isLiked(Post post, @Nullable Member member) {
        if (member == null) {
            return false;
        }
        return postLikeQueryRepository.existsByMemberAndPost(member, post);
    }

    public Page<PostSearchResponse> search(
            PostSearchCond cond,
            Pageable pageable,
            @Nullable Long memberId
    ) {
        Member member = memberQueryRepository.getMemberIfIdNotNull(memberId);
        return postQueryRepository.search(memberId, cond, pageable)
                .map(post -> {
                    try {
                        post.validateAccess(member, null);
                        return PostSearchResponse.from(post);
                    } catch (NoAuthorityPostException e) {
                        return PostSearchResponse.protectedPost(post);
                    }
                });
    }
}

