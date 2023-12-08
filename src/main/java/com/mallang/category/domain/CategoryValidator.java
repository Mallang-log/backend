package com.mallang.category.domain;

import static com.mallang.common.utils.ObjectsUtils.isNulls;
import static com.mallang.common.utils.ObjectsUtils.notEquals;
import static com.mallang.common.utils.ObjectsUtils.validateWhenNonNullWithFailCond;

import com.mallang.blog.domain.Blog;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateUpdateHierarchy(
            Category target,
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
        validateOwners(target, parent, prevSibling, nextSibling);
        validateSelfReference(target, parent, prevSibling, nextSibling);
        validateDescendantReference(target, parent, prevSibling, nextSibling);
        validateContinuous(prevSibling, nextSibling);
        validateParentAndChildRelation(target, parent, prevSibling, nextSibling);
        validateDuplicatedNameWhenParticipated(target, prevSibling, nextSibling);
    }

    private void validateOwners(Category target, Category... categories) {
        for (Category category : categories) {
            if (category != null) {
                target.validateOwner(category.getOwner());
            }
        }
    }

    private void validateSelfReference(Category target, Category parent, Category prevSibling, Category nextSibling) {
        if (target.equals(parent) || target.equals(prevSibling) || target.equals(nextSibling)) {
            throw new CategoryHierarchyViolationException("자기 자신 혹은 자손들을 부모, 혹은 형제로 지정할 수 없습니다.");
        }
    }

    private void validateDescendantReference(
            Category target,
            Category parent,
            Category prevSibling,
            Category nextSibling
    ) {
        List<Category> descendants = target.getDescendants();
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
            Category target,
            Category parent,
            Category prevSibling,
            Category nextSibling
    ) {
        if (isNulls(prevSibling, nextSibling)) {
            validateNoChildrenInParent(parent, target.getBlog());
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

    private void validateNoChildrenInParent(Category parent, Blog blog) {
        if (parent == null) {
            if (categoryRepository.existsByBlog(blog)) {
                throw new CategoryHierarchyViolationException("블로드에 존재하는 다른 최상위 카테고리와의 관계가 명시되지 않았습니다.");
            }
        } else {
            if (!parent.getChildren().isEmpty()) {
                throw new CategoryHierarchyViolationException("주어진 부모의 자식 카테고리와의 관계가 명시되지 않았습니다.");
            }
        }
    }

    private void validateDuplicatedNameWhenParticipated(Category target, Category prevSibling, Category nextSibling) {
        validateWhenNonNullWithFailCond(
                prevSibling,
                prev -> Objects.equals(target.getName(), prev.getName()),
                new DuplicateCategoryNameException("직전 형제 카테고리와 이름이 겹칩니다.")
        );
        validateWhenNonNullWithFailCond(
                nextSibling,
                next -> Objects.equals(target.getName(), next.getName()),
                new DuplicateCategoryNameException("다음 형제 카테고리와 이름이 겹칩니다.")
        );
    }
}
