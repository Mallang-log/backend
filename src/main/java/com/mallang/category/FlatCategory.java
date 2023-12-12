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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@MappedSuperclass
public abstract class FlatCategory<T extends FlatCategory<T>> extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    protected Long id;

    protected String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    protected Member owner;

    protected FlatCategory(String name, Member owner) {
        this.name = name;
        this.owner = owner;
    }

    public abstract void validateOwner(Member member);

    public void create(
            @Nullable T prevSibling,
            @Nullable T nextSibling,
            FlatCategoryValidator validator
    ) {
        if (isNulls(prevSibling, nextSibling)) {
            validator.validateNoCategories(owner);
            return;
        }
        validator.validateDuplicateName(owner, name);
        updateHierarchy(prevSibling, nextSibling);
    }

    public void updateHierarchy(
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        validateOwners(prevSibling, nextSibling);
        validateSelfReference(prevSibling, nextSibling);
        validateContinuous(prevSibling, nextSibling);
        withdrawCurrentHierarchy();
        participateHierarchy(prevSibling, nextSibling);
    }

    private void validateOwners(
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (prevSibling != null) {
            validateOwner(prevSibling.getOwner());
        }
        if (nextSibling != null) {
            validateOwner(nextSibling.getOwner());
        }
    }

    private void validateSelfReference(
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (equals(prevSibling) || equals(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신을 형제로 지정할 수 없습니다.");
        }
    }

    private void validateContinuous(@Nullable T prevSibling, @Nullable T nextSibling) {
        if (isNulls(prevSibling, nextSibling)) {
            throw new CategoryHierarchyViolationException("형제들이 제대로 명시되지 않았습니다.");
        }
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

    private void withdrawCurrentHierarchy() {
        if (getPreviousSibling() != null) {
            getPreviousSibling().setNextSibling(getNextSibling());
        }
        if (getNextSibling() != null) {
            getNextSibling().setPreviousSibling(getPreviousSibling());
        }
        setPreviousSibling(null);
        setNextSibling(null);
    }

    private void participateHierarchy(
            @Nullable T prevSibling,
            @Nullable T nextSibling
    ) {
        if (prevSibling != null) {
            prevSibling.setNextSibling(self());
        }
        if (nextSibling != null) {
            nextSibling.setPreviousSibling(self());
        }
        setPreviousSibling(prevSibling);
        setNextSibling(nextSibling);
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
        withdrawCurrentHierarchy();
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

    protected abstract T self();

    protected abstract void setPreviousSibling(T previousSibling);

    protected abstract void setNextSibling(T nextSibling);
}
