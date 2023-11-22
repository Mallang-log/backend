package com.mallang.subscribe.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.subscribe.application.command.BlogSubscribeCommand;
import com.mallang.subscribe.application.command.BlogUnsubscribeCommand;
import com.mallang.subscribe.domain.BlogSubscribe;
import com.mallang.subscribe.domain.BlogSubscribeRepository;
import com.mallang.subscribe.domain.BlogSubscribeValidator;
import com.mallang.subscribe.exception.UnsubscribeUnsubscribedBlogException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class BlogSubscribeService {

    private final BlogSubscribeRepository blogSubscribeRepository;
    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final BlogSubscribeValidator blogSubscribeValidator;

    public Long subscribe(BlogSubscribeCommand command) {
        Member member = memberRepository.getById(command.subscriberId());
        Blog blog = blogRepository.getById(command.blogId());
        BlogSubscribe blogSubscribe = new BlogSubscribe(member, blog);
        blogSubscribe.subscribe(blogSubscribeValidator);
        return blogSubscribeRepository.save(blogSubscribe).getId();
    }

    public void unsubscribe(BlogUnsubscribeCommand command) {
        BlogSubscribe subscribe = blogSubscribeRepository
                .findBySubscriberIdAndBlogId(command.subscriberId(), command.blogId())
                .orElseThrow(UnsubscribeUnsubscribedBlogException::new);
        blogSubscribeRepository.delete(subscribe);
    }
}
