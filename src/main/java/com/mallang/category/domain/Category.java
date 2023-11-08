package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.blog.domain.Blog;
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
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Category extends CommonDomainModel {

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(fetch = LAZY, mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    private Category(String name, Member member, Blog blog) {
        blog.validateOwner(member.getId());
        this.name = name;
        this.member = member;
        this.blog = blog;
    }

    public static Category create(
            String name,
            Member member,
            Blog blog,
            @Nullable Category parent,
            CategoryValidator validator
    ) {
        Category category = new Category(name, member, blog);
        category.setParent(parent, validator);
        return category;
    }

    private void setParent(@Nullable Category parent, CategoryValidator validator) {
        if (willBeRoot(parent)) {
            beRoot(validator);
            return;
        }
        beChild(parent);
    }

    private boolean willBeRoot(@Nullable Category parent) {
        return parent == null;
    }

    private void beRoot(CategoryValidator validator) {
        validator.validateDuplicateRootName(member.getId(), name);
        unlinkFromParent();
    }

    private void unlinkFromParent() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
            parent = null;
        }
    }

    private void beChild(Category parent) {
        validateOwner(parent.getMember().getId(), new NoAuthorityUseCategoryException());
        validateHierarchy(parent);
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
        if (getDescendants().contains(parent)) {
            throw new CategoryHierarchyViolationException();
        }
    }

    public List<Category> getDescendants() {
        List<Category> children = new ArrayList<>();
        if (getChildren().isEmpty()) {
            return children;
        }
        for (Category child : getChildren()) {
            children.add(child);
            children.addAll(child.getDescendants());
        }
        return children;
    }

    private void link(Category parent) {
        unlinkFromParent();
        validateDuplicatedNameInSameHierarchy(parent);
        this.parent = parent;
        getParent().getChildren().add(this);
    }

    private void validateDuplicatedNameInSameHierarchy(Category parent) {
        parent.getChildren().stream()
                .filter(it -> it.getName().equals(name))
                .findAny()
                .ifPresent(it -> {
                    throw new DuplicateCategoryNameException();
                });
    }

    public void update(Long memberId, String name, Category parent, CategoryValidator validator) {
        validateOwner(memberId, new NoAuthorityUpdateCategoryException());
        this.name = name;
        setParent(parent, validator);
    }

    public void delete(Long memberId) {
        validateOwner(memberId, new NoAuthorityDeleteCategoryException());
        validateNoChildren();
        unlinkFromParent();
        registerEvent(new CategoryDeletedEvent(getId()));
    }

    private void validateNoChildren() {
        if (!children.isEmpty()) {
            throw new ChildCategoryExistException();
        }
    }
}
