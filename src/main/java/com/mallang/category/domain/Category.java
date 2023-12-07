package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.common.domain.CommonRootEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class Category extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "previous_sibling_id")
    private Category previousSibling;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "next_sibling_id")
    private Category nextSibling;

    public Category(String name, Member owner, Blog blog) {
        this.name = name;
        this.owner = owner;
        this.blog = blog;
        blog.validateOwner(owner);
    }

    public void validateOwner(Member member) {
        if (!owner.equals(member)) {
            throw new NoAuthorityCategoryException();
        }
    }

    public void updateHierarchy(
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling,
            CategoryValidator validator
    ) {
        validator.validateUpdateHierarchy(this, parent, prevSibling, nextSibling);
        withdrawCurrentHierarchy();
        participateHierarchy(parent, prevSibling, nextSibling);
    }

    private void withdrawCurrentHierarchy() {
        if (previousSibling != null) {
            previousSibling.setNextSibling(nextSibling);
        }
        if (nextSibling != null) {
            nextSibling.setPreviousSibling(previousSibling);
        }
        if (parent != null) {
            parent.getChildren().remove(this);
        }
        previousSibling = null;
        nextSibling = null;
        parent = null;
    }

    private void participateHierarchy(
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
        if (prevSibling != null) {
            prevSibling.setNextSibling(this);
        }
        if (nextSibling != null) {
            nextSibling.setPreviousSibling(this);
        }
        if (parent != null) {
            parent.getChildren().add(this);
        }
        this.previousSibling = prevSibling;
        this.nextSibling = nextSibling;
        this.parent = parent;
    }

    public void updateName(String name, CategoryValidator validator) {
        validator.validateDuplicateNameInSibling(this, name);
        this.name = name;
    }

    public void delete() {
        validateNoChildren();
        unlinkFromParent();
        registerEvent(new CategoryDeletedEvent(getId()));
    }

    private void unlinkFromParent() {
        if (parent != null) {
            parent.children.remove(this);
            parent = null;
        }
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

    public List<Category> getSortedChildren() {
        Optional<Category> first = getChildren()
                .stream()
                .filter(it -> it.getPreviousSibling() == null)
                .findAny();
        if (first.isEmpty()) {
            return new ArrayList<>();
        }
        List<Category> categories = new ArrayList<>();
        Category current = first.get();
        categories.add(current);
        while (current.getNextSibling() != null) {
            current = current.getNextSibling();
            categories.add(current);
        }
        return categories;
    }

    // For lazy loading issue
    // parent, prev, next 가 지연로딩되어 프록시로 조회되므로, 그냥 사용 시 update 가 동작하지 않음
    // 이를 해결하기 위해 메서드를 통해 접근해야 하는데, private 혹은 package-private 인 경우 여전히 동작하지 않음
    // 따라서 protected 로 설정한
    protected void setPreviousSibling(Category previousSibling) {
        this.previousSibling = previousSibling;
    }

    protected void setNextSibling(Category nextSibling) {
        this.nextSibling = nextSibling;
    }
}
