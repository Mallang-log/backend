package com.mallang.post.query.repository;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QPostCategory.postCategory;
import static com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond.NO_CATEGORY_CONDITION;

import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

public interface PostManageSearchDao {

    Page<Post> searchForManage(Blog blog, PostManageSearchCond cond, Pageable pageable);

    @Builder
    record PostManageSearchCond(
            @Nullable String title,
            @Nullable String bodyText,
            @Nullable Long categoryId,
            @Nullable Visibility visibility
    ) {
        public static final long NO_CATEGORY_CONDITION = -1L;
    }

    @RequiredArgsConstructor
    @Repository
    class PostManageSearchDaoImpl implements PostManageSearchDao {

        private final JPAQueryFactory query;
        private final PostCategoryQueryRepository postCategoryQueryRepository;

        @Override
        public Page<Post> searchForManage(Blog blog, PostManageSearchCond cond, Pageable pageable) {
            JPAQuery<Long> countQuery = query.select(post.countDistinct())
                    .from(post)
                    .where(
                            blogEq(blog),
                            hasCategory(cond.categoryId()),
                            titleContains(cond.title()),
                            bodyTextContains(cond.bodyText()),
                            visibilityEq(cond.visibility())
                    );
            List<Post> result = query.selectFrom(post)
                    .distinct()
                    .leftJoin(post.content.category, postCategory).fetchJoin()
                    .where(
                            blogEq(blog),
                            hasCategory(cond.categoryId()),
                            titleContains(cond.title()),
                            bodyTextContains(cond.bodyText()),
                            visibilityEq(cond.visibility())
                    )
                    .orderBy(post.id.postId.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        }

        private BooleanExpression blogEq(Blog blog) {
            return post.blog.eq(blog);
        }

        private BooleanExpression hasCategory(@Nullable Long categoryId) {
            if (categoryId == null) {
                return null;
            }
            if (categoryId == NO_CATEGORY_CONDITION) {
                return post.content.category.isNull();
            }
            List<Long> categoryIds = postCategoryQueryRepository.getIdsWithDescendants(categoryId);
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
