package com.mallang.blog.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mallang.auth.domain.Member;
import com.mallang.blog.exception.NoAuthorityAboutException;
import com.mallang.common.domain.CommonRootEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class About extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false, unique = true)
    private Blog blog;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writer;

    public About(Blog blog, String content, Member writer) {
        this.blog = blog;
        this.content = content;
        this.writer = writer;
        blog.validateOwner(writer);
    }

    public void write(AboutValidator validator) {
        validator.validateAlreadyExist(blog);
    }

    public void update(String content) {
        this.content = content;
    }

    public void validateWriter(Member member) {
        if (!writer.equals(member)) {
            throw new NoAuthorityAboutException();
        }
    }
}
