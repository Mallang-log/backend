package com.mallang.post.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostStarQueryRepository;
import com.mallang.post.query.response.StaredPostResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostStarQueryService {

    private final MemberQueryRepository memberQueryRepository;
    private final PostStarQueryRepository postStarQueryRepository;

    public Page<StaredPostResponse> findAllByMemberId(
            Long targetMemberId,
            @Nullable Long requesterId,
            Pageable pageable
    ) {
        Member member = memberQueryRepository.getMemberIfIdNotNull(requesterId);
        return postStarQueryRepository.findAllByMemberId(targetMemberId, pageable)
                .map(postStar -> {
                    try {
                        Post post = postStar.getPost();
                        post.validateAccess(member, null);
                        return StaredPostResponse.from(postStar);
                    } catch (NoAuthorityPostException e) {
                        return StaredPostResponse.protectedPost(postStar);
                    }
                });
    }
}

