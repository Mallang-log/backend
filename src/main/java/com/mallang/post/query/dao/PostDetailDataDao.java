package com.mallang.post.query.dao;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.query.dao.support.PostQuerySupport;
import com.mallang.post.query.data.PostDetailData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostDetailDataDao {

    private final PostQuerySupport postQuerySupport;

    public PostDetailData find(BlogName blogName, Long id) {
        return PostDetailData.from(postQuerySupport.getByBlogNameAndId(blogName, id));
    }
}
