package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityDeleteCategoryException;
import com.mallang.category.exception.NoAuthorityUpdateCategoryException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.common.execption.MallangLogException;
import com.mallang.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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
    private List<Category> children = new ArrayList<>();

    private Category(String name, Member member) {
        this.name = name;
        this.member = member;
    }

    public static Category create(String name, Member member, Category parent, CategoryValidator validator) {
        Category category = new Category(name, member);
        category.setParent(parent, validator);
        return category;
    }

    private void setParent(Category parent, CategoryValidator validator) {
        if (willBeRoot(parent)) {
            beRoot(validator);
            return;
        }
        beChild(parent);
    }

    private boolean willBeRoot(Category parent) {
        return parent == null;
    }

    private void beRoot(CategoryValidator validator) {
        validator.validateDuplicateRootName(member.getId(), name);
        unlinkExistParent();
        parent = null;
    }

    private void unlinkExistParent() {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
    }

    private void beChild(Category parent) {
        validateOwner(parent.getMember().getId(), new NoAuthorityUseCategoryException());
        validateHierarchy(parent);
        unlinkExistParent();
        link(parent);
    }

    private void validateOwner(Long memberId, MallangLogException e) {
        if (!member.getId().equals(memberId)) {
            throw e;
        }
    }

    private void validateHierarchy(Category parent) {
        if (this.equals(parent)) {
            throw new CategoryHierarchyViolationException();
        }
        if (getChildren().contains(parent)) {
            throw new CategoryHierarchyViolationException();
        }
        for (Category child : getChildren()) {
            child.validateHierarchy(parent);
        }
    }

    private void link(Category parent) {
        this.parent = parent;
        this.parent.addChild(this);
        validateDuplicatedNameInChildren(name);
    }

    private void validateDuplicatedNameInChildren(String name) {
        long duplicatedNameCount = parent.getChildren().stream()
                .filter(it -> it.getName().equals(name))
                .count();
        if (duplicatedNameCount > 1) {
            throw new DuplicateCategoryNameException();
        }
    }

    private void addChild(Category child) {
        children.add(child);
    }

    private void removeChild(Category child) {
        children.remove(child);
    }

    public void update(Long memberId, String name, Category parent, CategoryValidator validator) {
        validateOwner(memberId, new NoAuthorityUpdateCategoryException());
        this.name = name;
        setParent(parent, validator);
    }

    public void delete(Long memberId) {
        validateOwner(memberId, new NoAuthorityDeleteCategoryException());
        validateNoChildren();
        unlinkExistParent();
        registerEvent(new CategoryDeletedEvent(getId()));
    }

    private void validateNoChildren() {
        if (!children.isEmpty()) {
            throw new ChildCategoryExistException();
        }
    }
}
