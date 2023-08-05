package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.category.application.exception.NoAuthorityUseCategory;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Category extends CommonDomainModel {

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Builder
    public Category(String name, Member member) {
        this.name = name;
        this.member = member;
    }

    public void setParent(Category parent) {
        validateSameMember(parent);
        this.parent = parent;
    }

    private void validateSameMember(Category parent) {
        if (!parent.getMember().getId().equals(member.getId())) {
            throw new NoAuthorityUseCategory();
        }
    }
}
