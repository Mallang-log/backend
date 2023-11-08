package com.mallang.blog.application;

import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogName;
import com.mallang.blog.domain.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class BlogServiceTestHelper {

    private final BlogService blogService;
    private final BlogRepository blogRepository;

    public BlogName 블로그_개설후_이름_반환(Long memberId, String name) {
        blogService.open(new OpenBlogCommand(memberId, name));
        return new BlogName(name);
    }

    public Blog 블로그_개설(Long memberId, String name) {
        return blogRepository.getById(blogService.open(new OpenBlogCommand(memberId, name)));
    }
}
