package com.mallang.blog.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.application.command.DeleteAboutCommand;
import com.mallang.blog.application.command.UpdateAboutCommand;
import com.mallang.blog.application.command.WriteAboutCommand;
import com.mallang.blog.domain.About;
import com.mallang.blog.domain.AboutRepository;
import com.mallang.blog.domain.AboutValidator;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AboutService {

    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
    private final AboutRepository aboutRepository;
    private final AboutValidator aboutValidator;

    public Long write(WriteAboutCommand command) {
        Member member = memberRepository.getById(command.memberId());
        Blog blog = blogRepository.getByIdAndOwnerId(command.blogId(), command.memberId());
        About about = command.toAbout(member, blog);
        about.write(aboutValidator);
        return aboutRepository.save(about)
                .getId();
    }

    public void update(UpdateAboutCommand command) {
        About about = aboutRepository.getByIdAndWriterIdAndBlogId(
                command.aboutId(), command.memberId(), command.blogId()
        );
        about.update(command.content());
    }

    public void delete(DeleteAboutCommand command) {
        About about = aboutRepository.getByIdAndWriterIdAndBlogId(
                command.aboutId(), command.memberId(), command.blogId()
        );
        aboutRepository.delete(about);
    }
}
