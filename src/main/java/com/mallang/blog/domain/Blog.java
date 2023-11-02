package com.mallang.blog.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.blog.exception.IsNotBlogOwnerException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Blog extends CommonDomainModel {

    @Column(unique = true)
    private BlogName name;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @Builder
    public Blog(String name, Member owner) {
        this.name = new BlogName(name);
        this.owner = owner;
    }

    public void open(BlogValidator blogValidator) {
        blogValidator.validateOpen(this.owner.getId(), this.name);
    }

    public void validateOwner(Long memberId) {
        if (!getOwner().getId().equals(memberId)) {
            throw new IsNotBlogOwnerException();
        }
    }
}
