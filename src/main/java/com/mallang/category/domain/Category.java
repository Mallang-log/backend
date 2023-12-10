package com.mallang.category.domain;

import static com.mallang.common.utils.ObjectsUtils.isNulls;
import static com.mallang.common.utils.ObjectsUtils.notEquals;
import static com.mallang.common.utils.ObjectsUtils.validateWhenNonNullWithFailCond;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
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
import java.util.Objects;
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
            @Nullable Category nextSibling
    ) {
        validateOwners(parent, prevSibling, nextSibling);
        validateSelfOrDescendantReference(parent, prevSibling, nextSibling);
        validateContinuous(prevSibling, nextSibling);
        validateParentAndChildRelation(parent, prevSibling, nextSibling);
        validateDuplicatedNameWhenParticipated(prevSibling, nextSibling);
        withdrawCurrentHierarchy();
        participateHierarchy(parent, prevSibling, nextSibling);
    }

    private void validateOwners(Category... categories) {
        for (Category category : categories) {
            if (category != null) {
                validateOwner(category.getOwner());
            }
        }
    }

    private void validateSelfOrDescendantReference(
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
        if (equals(parent) || equals(prevSibling) || equals(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
        }
        List<Category> descendants = getDescendants();
        if (descendants.contains(parent) || descendants.contains(prevSibling) || descendants.contains(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
        }
    }

    private void validateContinuous(@Nullable Category prevSibling, @Nullable Category nextSibling) {
        validateWhenNonNullWithFailCond(
                prevSibling,
                prev -> notEquals(prev.getNextSibling(), nextSibling),
                new CategoryHierarchyViolationException("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.")
        );
        validateWhenNonNullWithFailCond(
                nextSibling,
                next -> notEquals(next.getPreviousSibling(), prevSibling),
                new CategoryHierarchyViolationException("이전 카테고리과 다음 카테고리가 연속적이지 않습니다.")
        );
    }

    private void validateParentAndChildRelation(
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
        if (isNulls(prevSibling, nextSibling)) {
            validateNoChildrenInParent(parent);
        }
        validateWhenNonNullWithFailCond(
                prevSibling,
                prev -> notEquals(prev.getParent(), parent),
                new CategoryHierarchyViolationException("주어진 형제와 부모의 관계가 올바르지 않습니다.")
        );
        validateWhenNonNullWithFailCond(
                nextSibling,
                next -> notEquals(next.getParent(), parent),
                new CategoryHierarchyViolationException("주어진 형제와 부모의 관계가 올바르지 않습니다.")
        );
    }

    private void validateNoChildrenInParent(Category parent) {
        if (parent == null) {
            Category root = getRoot();
            if (root.getPreviousSibling() == null && root.getNextSibling() == null) {
                if (equals(root)) {
                    return;
                }
            }
            throw new CategoryHierarchyViolationException("블로드에 존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
        } else {
            if (!parent.getChildren().isEmpty()) {
                throw new CategoryHierarchyViolationException("주어진 부모의 자식 카테고리와의 관계가 명시되지 않았습니다.");
            }
        }
    }

    private Category getRoot() {
        Category root = this;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    private void validateDuplicatedNameWhenParticipated(
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
        validateWhenNonNullWithFailCond(
                prevSibling,
                prev -> Objects.equals(getName(), prev.getName()),
                new DuplicateCategoryNameException("직전 형제 카테고리와 이름이 겹칩니다.")
        );
        validateWhenNonNullWithFailCond(
                nextSibling,
                next -> Objects.equals(getName(), next.getName()),
                new DuplicateCategoryNameException("다음 형제 카테고리와 이름이 겹칩니다.")
        );
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

    public void updateName(String name) {
        List<Category> siblings = getSiblings();
        while (!siblings.isEmpty()) {
            Category sibling = siblings.removeLast();
            if (sibling.getName().equals(name)) {
                throw new DuplicateCategoryNameException();
            }
        }
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
        while (current != null) {
            categories.add(current);
            current = current.getNextSibling();
        }
        return categories;
    }

    public List<Category> getSiblings() {
        List<Category> siblings = new ArrayList<>();
        Category currentPrev = this.previousSibling;
        while (currentPrev != null) {
            siblings.addFirst(currentPrev);
            currentPrev = currentPrev.previousSibling;
        }
        Category currentNext = this.nextSibling;
        while (currentNext != null) {
            siblings.addLast(currentNext);
            currentNext = currentNext.nextSibling;
        }
        return siblings;
    }

    // lazy loading issue 해결을 위한 메서드
    // 카테고리 조회 시 parent, prev, next 가 지연로딩되어 프록시로 조회되므로, prev.next = this 등으로 사용 시 update 가 동작하지 않음
    // 이를 해결하기 위해 메서드를 통해 접근해야 하는데 private 혹은 package-private 메서드의 경우 여전히 동작하지 않음
    // 따라서 protected 로 설정함
    protected void setPreviousSibling(Category previousSibling) {
        this.previousSibling = previousSibling;
    }

    protected void setNextSibling(Category nextSibling) {
        this.nextSibling = nextSibling;
    }
}
