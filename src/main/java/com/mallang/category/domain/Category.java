package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.common.domain.CommonDomainModel;
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
    @JoinColumn(name = "owner_id", nullable = false)
    private Member owner;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(fetch = LAZY, mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    private Category(String name, Member owner, Blog blog) {
        this.name = name;
        this.owner = owner;
        this.blog = blog;
        blog.validateOwner(owner);
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

    public void update(String name, Category parent, CategoryValidator validator) {
        this.name = name;
        setParent(parent, validator);
    }

    public void delete() {
        validateNoChildren();
        unlinkFromParent();
        registerEvent(new CategoryDeletedEvent(getId()));
    }

    private void setParent(@Nullable Category parent, CategoryValidator validator) {
        if (parent == null) {
            beRoot(validator);
            return;
        }
        beChild(parent);
    }

    private void beRoot(CategoryValidator validator) {
        validator.validateDuplicateRootName(owner.getId(), name);
        unlinkFromParent();
    }

    private void unlinkFromParent() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
            parent = null;
        }
    }

    private void beChild(Category parent) {
        parent.validateOwner(owner);
        validateHierarchy(parent);
        link(parent);
    }

    public void validateOwner(Member member) {
        if (!owner.equals(member)) {
            throw new NoAuthorityCategoryException();
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

    private void validateNoChildren() {
        if (!children.isEmpty()) {
            throw new ChildCategoryExistException();
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
}
