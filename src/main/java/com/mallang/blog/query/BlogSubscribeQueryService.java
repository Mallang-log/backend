package com.mallang.blog.query;

import com.mallang.blog.query.repository.BlogSubscribeQueryRepository;
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

    private final BlogSubscribeQueryRepository blogSubscribeQueryRepository;

    public Page<SubscriberResponse> findSubscribers(String blogName, Pageable pageable) {
        return blogSubscribeQueryRepository.findSubscribers(blogName, pageable)
                .map(SubscriberResponse::from);
    }

    public Page<SubscribingBlogResponse> findSubscribingBlogs(Long memberId, Pageable pageable) {
        return blogSubscribeQueryRepository.findSubscribingBlogs(memberId, pageable)
                .map(SubscribingBlogResponse::from);
    }
}
