package com.mallang.subscribe.query;

import com.mallang.subscribe.query.dao.SubscriberDataDao;
import com.mallang.subscribe.query.dao.SubscribingBlogDataDao;
import com.mallang.subscribe.query.data.SubscriberData;
import com.mallang.subscribe.query.data.SubscribingBlogData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlogSubscribeQueryService {

    private final SubscriberDataDao subscriberDataDao;
    private final SubscribingBlogDataDao subscribingBlogDataDao;

    public List<SubscriberData> findSubscribers(String blogName) {
        return subscriberDataDao.findSubscribers(blogName);
    }

    public List<SubscribingBlogData> findSubscribingBlogs(Long memberId) {
        return subscribingBlogDataDao.findSubscribingBlogs(memberId);
    }
}
