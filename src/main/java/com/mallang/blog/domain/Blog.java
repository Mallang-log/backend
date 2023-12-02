package com.mallang.blog.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.domain.CommonRootEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Blog extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true)
    private BlogName name;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @Builder
    public Blog(String name, Member owner) {
        this.name = new BlogName(name);
        this.owner = owner;
    }

    public void open(BlogValidator blogValidator) {
        blogValidator.validateOpen(this.owner.getId(), this.name);
    }

    public void validateOwner(Member member) {
        if (!owner.equals(member)) {
            throw new NoAuthorityBlogException();
        }
    }

    public String getName() {
        return name.getValue();
    }
}
