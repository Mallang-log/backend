package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(fetch = LAZY, mappedBy = "parent")
    private final List<Category> children = new ArrayList<>();

    @Builder
    public Category(String name, Member member) {
        this.name = name;
        this.member = member;
    }

    public void setParent(Category parent) {
        validateSameMember(parent);
        validateHierarchy(parent);
        this.parent = parent;
        parent.addChild(this);
    }


    private void validateSameMember(Category parent) {
        if (!parent.getMember().getId().equals(member.getId())) {
            throw new NoAuthorityUseCategoryException();
        }
    }

    private void validateHierarchy(Category parent) {
        if (children.contains(parent)) {
            throw new CategoryHierarchyViolationException();
        }
        for (Category child : children) {
            child.validateHierarchy(parent);
        }
    }

    private void addChild(Category child) {
        this.children.add(child);
    }
}
