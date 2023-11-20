package com.mallang.blog.application;

import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class BlogServiceTestHelper {

    private final BlogService blogService;
    private final BlogRepository blogRepository;

    @Transactional
    public Blog 블로그_개설(Long memberId, String name) {
        Blog blog = blogRepository.getById(blogService.open(new OpenBlogCommand(memberId, name)));
        blog.getName();  // 초기화
        return blog;
    }
}
