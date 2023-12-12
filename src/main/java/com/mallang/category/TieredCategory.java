package com.mallang.category;

import static com.mallang.common.utils.ObjectsUtils.isNulls;
import static com.mallang.common.utils.ObjectsUtils.notEquals;
import static com.mallang.common.utils.ObjectsUtils.validateWhenNonNullWithFailCond;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonRootEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@MappedSuperclass
public abstract class TieredCategory<T extends TieredCategory<T>> extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;

    protected String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    protected Member owner;

    protected TieredCategory(String name, Member owner) {
        this.name = name;
        this.owner = owner;
    }

    public void create(
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling,
            TieredCategoryValidator validator
    ) {
        if (isNulls(parent, prevSibling, nextSibling)) {
            validator.validateNoCategories(owner);
            return;
        }
        updateHierarchy(parent, prevSibling, nextSibling);
    }

    public abstract void validateOwner(Member member);

    public void updateHierarchy(
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        validateOwners(parent, prevSibling, nextSibling);
        validateSelfOrDescendantReference(parent, prevSibling, nextSibling);
        validateContinuous(prevSibling, nextSibling);
        validateParentAndChildRelation(parent, prevSibling, nextSibling);
        validateDuplicatedNameWhenParticipated(prevSibling, nextSibling);
        withdrawCurrentHierarchy();
        participateHierarchy(parent, prevSibling, nextSibling);
    }

    private void validateOwners(
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (parent != null) {
            validateOwner(parent.getOwner());
        }
        if (prevSibling != null) {
            validateOwner(prevSibling.getOwner());
        }
        if (nextSibling != null) {
            validateOwner(nextSibling.getOwner());
        }
    }

    private void validateSelfOrDescendantReference(
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (equals(parent) || equals(prevSibling) || equals(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
        }
        List<T> descendants = getDescendantsExceptSelf();
        if (descendants.contains(parent) || descendants.contains(prevSibling) || descendants.contains(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
        }
    }

    private void validateContinuous(@Nullable T prevSibling, @Nullable T nextSibling) {
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
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (isNulls(prevSibling, nextSibling)) {
            if (parent == null) {
                throw new CategoryHierarchyViolationException("카테고리 계층구조 변경 시 부모나 형제들 중 최소 하나와의 관계가 주어져야 합니다.");
            }
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


    private void validateNoChildrenInParent(T parent) {
        if (!parent.getChildren().isEmpty()) {
            throw new CategoryHierarchyViolationException("주어진 부모의 자식 카테고리와의 관계가 명시되지 않았습니다.");
        }
    }

    private void validateDuplicatedNameWhenParticipated(
            @Nullable T prevSibling,
            @Nullable T nextSibling
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
        if (getPreviousSibling() != null) {
            getPreviousSibling().setNextSibling(getNextSibling());
        }
        if (getNextSibling() != null) {
            getNextSibling().setPreviousSibling(getPreviousSibling());
        }
        if (getParent() != null) {
            getParent().getChildren().remove(self());
        }
        setPreviousSibling(null);
        setNextSibling(null);
        setParent(null);
    }

    private void participateHierarchy(
            @Nullable T parent,
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (prevSibling != null) {
            prevSibling.setNextSibling(self());
        }
        if (nextSibling != null) {
            nextSibling.setPreviousSibling(self());
        }
        if (parent != null) {
            parent.getChildren().add(self());
        }
        setPreviousSibling(prevSibling);
        setNextSibling(nextSibling);
        setParent(parent);
    }

    public void updateName(String name) {
        List<T> siblings = getSiblingsExceptSelf();
        while (!siblings.isEmpty()) {
            T sibling = siblings.removeLast();
            if (sibling.getName().equals(name)) {
                throw new DuplicateCategoryNameException();
            }
        }
        this.name = name;
    }

    public void delete() {
        validateNoChildren();
        withdrawCurrentHierarchy();
    }

    private void validateNoChildren() {
        if (!getChildren().isEmpty()) {
            throw new ChildCategoryExistException();
        }
    }

    public List<T> getDescendantsExceptSelf() {
        List<T> children = new ArrayList<>();
        if (getChildren().isEmpty()) {
            return children;
        }
        for (T child : getChildren()) {
            children.add(child);
            children.addAll(child.getDescendantsExceptSelf());
        }
        return children;
    }

    public List<T> getSortedChildren() {
        Optional<T> first = getChildren()
                .stream()
                .filter(it -> it.getPreviousSibling() == null)
                .findAny();
        if (first.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> categories = new ArrayList<>();
        T current = first.get();
        while (current != null) {
            categories.add(current);
            current = current.getNextSibling();
        }
        return categories;
    }

    public List<T> getSiblingsExceptSelf() {
        List<T> siblings = new ArrayList<>();
        T currentPrev = getPreviousSibling();
        while (currentPrev != null) {
            siblings.addFirst(currentPrev);
            currentPrev = currentPrev.getPreviousSibling();
        }
        T currentNext = this.getNextSibling();
        while (currentNext != null) {
            siblings.addLast(currentNext);
            currentNext = currentNext.getNextSibling();
        }
        return siblings;
    }

    public abstract T getPreviousSibling();

    public abstract T getNextSibling();

    public abstract T getParent();

    public abstract List<T> getChildren();

    protected abstract T self();

    protected abstract void setParent(T t);

    protected abstract void setPreviousSibling(T previousSibling);

    protected abstract void setNextSibling(T nextSibling);
}
