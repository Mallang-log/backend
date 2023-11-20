package com.mallang.post.query.dao;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.query.data.PostManageSearchCond.NO_CATEGORY_CONDITION;

import com.mallang.category.domain.Category;
import com.mallang.category.query.dao.support.CategoryQuerySupport;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.data.PostManageSearchCond;
import com.mallang.post.query.data.PostManageSimpleData;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostManageSimpleDataDao {

    private final JPAQueryFactory query;
    private final CategoryQuerySupport categoryQuerySupport;

    public List<PostManageSimpleData> search(Long memberId, PostManageSearchCond cond) {
        return query.selectFrom(post)
                .where(
                        memberAndBlogEq(memberId, cond.blogName()),
                        hasCategory(cond.categoryId()),
                        titleContains(cond.title()),
                        contentContains(cond.content()),
                        visibilityEq(cond.visibility())
                )
                .orderBy(post.id.desc())
                .fetch()
                .stream()
                .map(PostManageSimpleData::from)
                .toList();
    }

    private BooleanExpression memberAndBlogEq(Long memberId, String blogName) {
        return post.writer.id.eq(memberId).and(post.blog.name.value.eq(blogName));
    }

    private BooleanExpression hasCategory(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        if (categoryId == NO_CATEGORY_CONDITION) {
            return post.category.isNull();
        }
        Category category = categoryQuerySupport.getById(categoryId);
        List<Category> descendants = category.getDescendants();
        descendants.add(category);
        List<Long> categoryIds = descendants.stream()
                .map(CommonDomainModel::getId)
                .toList();
        return post.category.id.in(categoryIds);
    }

    private BooleanExpression titleContains(@Nullable String title) {
        if (ObjectUtils.isEmpty(title)) {
            return null;
        }
        return post.title.containsIgnoreCase(title);
    }

    private BooleanExpression contentContains(@Nullable String content) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        return post.content.containsIgnoreCase(content);
    }

    private BooleanExpression visibilityEq(@Nullable Visibility visibility) {
        if (visibility == null) {
            return null;
        }
        return post.visibilityPolish.visibility.eq(visibility);
    }
}
