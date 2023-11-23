package com.mallang.blog.domain.subscribe;

import com.mallang.blog.exception.AlreadySubscribedException;
import com.mallang.blog.exception.SelfSubscribeException;
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
