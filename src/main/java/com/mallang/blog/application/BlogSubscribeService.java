package com.mallang.blog.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.application.command.BlogSubscribeCommand;
import com.mallang.blog.application.command.BlogUnsubscribeCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.mallang.blog.domain.subscribe.BlogSubscribeRepository;
import com.mallang.blog.domain.subscribe.BlogSubscribeValidator;
import com.mallang.blog.exception.UnsubscribeUnsubscribedBlogException;
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
        Blog blog = blogRepository.getByName(command.blogName());
        BlogSubscribe blogSubscribe = new BlogSubscribe(member, blog);
        blogSubscribe.subscribe(blogSubscribeValidator);
        return blogSubscribeRepository.save(blogSubscribe).getId();
    }

    public void unsubscribe(BlogUnsubscribeCommand command) {
        BlogSubscribe subscribe = blogSubscribeRepository
                .findBySubscriberIdAndBlogName(command.subscriberId(), command.blogName())
                .orElseThrow(UnsubscribeUnsubscribedBlogException::new);
        blogSubscribeRepository.delete(subscribe);
    }
}
