package com.mallang.post.query.repository;

import static com.mallang.category.domain.QCategory.category;
import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond.NO_CATEGORY_CONDITION;

import com.mallang.category.query.repository.CategoryQueryRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
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
import org.springframework.util.ObjectUtils;

public interface PostManageSearchDao {

    Page<Post> searchForManage(Long memberId, PostManageSearchCond cond, Pageable pageable);

    @Builder
    record PostManageSearchCond(
            @NotNull String blogName,
            @Nullable String title,
            @Nullable String bodyText,
            @Nullable Long categoryId,
            @Nullable Visibility visibility
    ) {
        public static final long NO_CATEGORY_CONDITION = -1L;
    }

    @RequiredArgsConstructor
    @Component
    class PostManageSearchDaoImpl implements PostManageSearchDao {

        private final JPAQueryFactory query;
        private final CategoryQueryRepository categoryQueryRepository;

        @Override
        public Page<Post> searchForManage(Long memberId, PostManageSearchCond cond, Pageable pageable) {
            JPAQuery<Long> countQuery = query.select(post.countDistinct())
                    .from(post)
                    .where(
                            memberAndBlogEq(memberId, cond.blogName()),
                            hasCategory(cond.categoryId()),
                            titleContains(cond.title()),
                            bodyTextContains(cond.bodyText()),
                            visibilityEq(cond.visibility())
                    );
            List<Post> result = query.selectFrom(post)
                    .distinct()
                    .leftJoin(post.content.category, category).fetchJoin()
                    .where(
                            memberAndBlogEq(memberId, cond.blogName()),
                            hasCategory(cond.categoryId()),
                            titleContains(cond.title()),
                            bodyTextContains(cond.bodyText()),
                            visibilityEq(cond.visibility())
                    )
                    .orderBy(post.postId.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        }

        private BooleanExpression memberAndBlogEq(Long memberId, String blogName) {
            return post.content.writer.id.eq(memberId).and(post.blog.name.value.eq(blogName));
        }

        private BooleanExpression hasCategory(@Nullable Long categoryId) {
            if (categoryId == null) {
                return null;
            }
            if (categoryId == NO_CATEGORY_CONDITION) {
                return post.content.category.isNull();
            }
            List<Long> categoryIds = categoryQueryRepository.getCategoryAndDescendants(categoryId);
            return post.content.category.id.in(categoryIds);
        }

        private BooleanExpression titleContains(@Nullable String title) {
            if (ObjectUtils.isEmpty(title)) {
                return null;
            }
            return post.content.title.containsIgnoreCase(title);
        }

        private BooleanExpression bodyTextContains(@Nullable String bodyText) {
            if (ObjectUtils.isEmpty(bodyText)) {
                return null;
            }
            return post.content.bodyText.containsIgnoreCase(bodyText);
        }

        private BooleanExpression visibilityEq(@Nullable Visibility visibility) {
            if (visibility == null) {
                return null;
            }
            return post.visibilityPolish.visibility.eq(visibility);
        }
    }
}
