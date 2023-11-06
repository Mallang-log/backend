package com.mallang.post.query.dao;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.query.dao.support.PostLikeQuerySupport;
import com.mallang.post.query.dao.support.PostQuerySupport;
import com.mallang.post.query.data.PostDetailData;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostDetailDataDao {

    private final PostQuerySupport postQuerySupport;
    private final PostLikeQuerySupport postLikeQuerySupport;

    public PostDetailData find(@Nullable Long memberId, BlogName blogName, Long id) {
        if (memberId == null) {
            return PostDetailData.from(postQuerySupport.getByBlogNameAndId(blogName, id));
        }
        boolean isLiked = postLikeQuerySupport.existsByMemberIdAndBlogNameAndPostId(memberId, blogName, id);
        return PostDetailData.withLiked(postQuerySupport.getByBlogNameAndId(blogName, id), isLiked);
    }
}
