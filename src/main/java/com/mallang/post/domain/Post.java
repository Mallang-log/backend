package com.mallang.post.domain;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
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
