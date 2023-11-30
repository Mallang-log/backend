package com.mallang.blog.domain.subscribe;

import com.mallang.blog.exception.AlreadySubscribedException;
import com.mallang.blog.exception.SelfSubscribeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogSubscribeValidator {

    private final BlogSubscribeRepository blogSubscribeRepository;

    public void validateSubscribe(BlogSubscribe subscribe) {
        if (subscribe.getBlog().getOwner().equals(subscribe.getSubscriber())) {
            throw new SelfSubscribeException();
        }
        boolean alreadyExist = blogSubscribeRepository.existsBySubscriberAndBlog(
                subscribe.getSubscriber(),
                subscribe.getBlog()
        );
        if (alreadyExist) {
            throw new AlreadySubscribedException();
        }
    }
}
