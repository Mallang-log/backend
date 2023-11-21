package com.mallang.post.query.dao;

import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.support.PostLikeQuerySupport;
import com.mallang.post.query.support.PostQuerySupport;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostDetailDao {

    private final PostQuerySupport postQuerySupport;
    private final PostLikeQuerySupport postLikeQuerySupport;

    public PostDetailResponse find(@Nullable Long memberId, Long id) {
        if (memberId == null) {
            return PostDetailResponse.from(postQuerySupport.getById(id));
        }
        boolean isLiked = postLikeQuerySupport.existsByMemberIdAndPostId(memberId, id);
        return PostDetailResponse.withLiked(postQuerySupport.getById(id), isLiked);
    }
}
