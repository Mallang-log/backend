package com.mallang.subscribe.query;

import com.mallang.subscribe.query.dao.SubscriberDao;
import com.mallang.subscribe.query.dao.SubscribingBlogDao;
import com.mallang.subscribe.query.response.SubscriberResponse;
import com.mallang.subscribe.query.response.SubscribingBlogResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogSubscribeQueryService {

    private final SubscriberDao subscriberDao;
    private final SubscribingBlogDao subscribingBlogDataDao;

    public List<SubscriberResponse> findSubscribers(String blogName) {
        return subscriberDao.findSubscribers(blogName);
    }

    public List<SubscribingBlogResponse> findSubscribingBlogs(Long memberId) {
        return subscribingBlogDataDao.findSubscribingBlogs(memberId);
    }
}
