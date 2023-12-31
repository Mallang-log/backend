package com.mallang.blog.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.domain.BlogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;
    private final BlogValidator blogValidator;

    public Long open(OpenBlogCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = command.toBlog(member);
        blog.open(blogValidator);
        return blogRepository.save(blog).getId();
    }
}
