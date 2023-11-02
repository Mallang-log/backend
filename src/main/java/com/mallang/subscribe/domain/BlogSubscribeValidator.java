package com.mallang.subscribe.domain;

import com.mallang.subscribe.exception.AlreadySubscribedException;
import com.mallang.subscribe.exception.SelfSubscribeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogSubscribeValidator {

    private final BlogSubscribeRepository blogSubscribeRepository;

    public void validateSubscribe(BlogSubscribe blogSubscribe) {
        if (blogSubscribe.getBlog().getOwner().equals(blogSubscribe.getSubscriber())) {
            throw new SelfSubscribeException();
        }
        if (blogSubscribeRepository.existsBySubscriberAndBlog(
                blogSubscribe.getSubscriber(), blogSubscribe.getBlog()
        )) {
            throw new AlreadySubscribedException();
        }
    }
}
