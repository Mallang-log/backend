package com.mallang.blog.query;

import com.mallang.blog.query.dao.SubscriberDao;
import com.mallang.blog.query.dao.SubscribingBlogDao;
import com.mallang.blog.query.response.SubscriberResponse;
import com.mallang.blog.query.response.SubscribingBlogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogSubscribeQueryService {

    private final SubscriberDao subscriberDao;
    private final SubscribingBlogDao subscribingBlogDataDao;

    public Page<SubscriberResponse> findSubscribers(String blogName, Pageable pageable) {
        return subscriberDao.findSubscribers(blogName, pageable);
    }

    public Page<SubscribingBlogResponse> findSubscribingBlogs(Long memberId, Pageable pageable) {
        return subscribingBlogDataDao.findSubscribingBlogs(memberId, pageable);
    }
}
