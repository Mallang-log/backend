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

    public PostDetailResponse find(Long id, String blogName, @Nullable Long memberId) {
        if (memberId == null) {
            return PostDetailResponse.from(postQuerySupport.getByIdAndBlogName(id, blogName));
        }
        boolean isLiked = postLikeQuerySupport.existsByMemberIdAndPostId(memberId, id, blogName);
        return PostDetailResponse.withLiked(postQuerySupport.getByIdAndBlogName(id, blogName), isLiked);
    }
}
