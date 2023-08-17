package com.mallang.post.domain;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.category.domain.Category;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.common.execption.MallangLogException;
import com.mallang.member.domain.Member;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Post extends CommonDomainModel {

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = {PERSIST, REMOVE})
    private List<Tag> tags = new ArrayList<>();

    @Builder
    public Post(
            String title,
            String content,
            Member member,
            Category category,
            List<String> tags
    ) {
        this.title = title;
        this.content = content;
        this.member = member;
        setCategory(category);
        setTags(tags);
    }

    private void setTags(List<String> tags) {
        this.tags.clear();
        if (tags == null) {
            tags = Collections.emptyList();
        }
        tags.stream()
                .map(it -> new Tag(it, this))
                .forEach(it -> this.tags.add(it));
    }

    public void setCategory(Category category) {
        if (category == null) {
            this.category = null;
            return;
        }
        validateOwner(category.getMember().getId(), new NoAuthorityUseCategoryException());
        this.category = category;
    }

    public void update(
            Long memberId,
            String title,
            String content,
            Category category
    ) {
        validateOwner(memberId, new NoAuthorityUpdatePostException());
        setCategory(category);
        this.title = title;
        this.content = content;
    }

    private void validateOwner(Long memberId, MallangLogException e) {
        if (!member.getId().equals(memberId)) {
            throw e;
        }
    }
}
