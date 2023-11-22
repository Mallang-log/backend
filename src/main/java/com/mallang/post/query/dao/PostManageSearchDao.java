package com.mallang.post.query.dao;

import static com.mallang.category.domain.QCategory.category;
import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.query.dao.PostManageSearchDao.PostManageSearchCond.NO_CATEGORY_CONDITION;

import com.mallang.category.query.support.CategoryQuerySupport;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.query.response.PostManageSearchResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostManageSearchDao {

    private final JPAQueryFactory query;
    private final CategoryQuerySupport categoryQuerySupport;

    public Page<PostManageSearchResponse> search(Long memberId, PostManageSearchCond cond, Pageable pageable) {
        JPAQuery<Long> countQuery = query.select(post.countDistinct())
                .from(post)
                .where(
                        memberAndBlogEq(memberId, cond.blogId()),
                        hasCategory(cond.categoryId()),
                        titleContains(cond.title()),
                        contentContains(cond.content()),
                        visibilityEq(cond.visibility())
                );
        List<Post> result = query.selectFrom(post)
                .distinct()
                .leftJoin(post.category, category).fetchJoin()
                .where(
                        memberAndBlogEq(memberId, cond.blogId()),
                        hasCategory(cond.categoryId()),
                        titleContains(cond.title()),
                        contentContains(cond.content()),
                        visibilityEq(cond.visibility())
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne)
                .map(PostManageSearchResponse::from);
    }

    private BooleanExpression memberAndBlogEq(Long memberId, Long blogId) {
        return post.writer.id.eq(memberId).and(post.blog.id.eq(blogId));
    }

    private BooleanExpression hasCategory(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        if (categoryId == NO_CATEGORY_CONDITION) {
            return post.category.isNull();
        }
        List<Long> categoryIds = categoryQuerySupport.getCategoryAndDescendants(categoryId);
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

    @Builder
    public record PostManageSearchCond(
            @NotNull Long blogId,
            @Nullable String title,
            @Nullable String content,
            @Nullable Long categoryId,
            @Nullable Visibility visibility
    ) {
        public static final long NO_CATEGORY_CONDITION = -1L;
    }
}
