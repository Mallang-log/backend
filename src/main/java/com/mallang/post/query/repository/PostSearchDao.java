package com.mallang.post.query.repository;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.blog.domain.QBlog.blog;
import static com.mallang.category.domain.QCategory.category;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QTag.tag;
import static org.springframework.data.support.PageableExecutionUtils.getPage;

import com.mallang.category.query.repository.CategoryQueryRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.BadPostSearchCondException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

public interface PostSearchDao {

    Page<Post> search(@Nullable Long memberId, PostSearchCond cond, Pageable pageable);

    @Builder
    record PostSearchCond(
            @Nullable String blogName,
            @Nullable Long writerId,
            @Nullable Long categoryId,
            @Nullable String tag,
            @Nullable String title,
            @Nullable String bodyText,
            @Nullable String titleOrBodyText
    ) {
    }

    @RequiredArgsConstructor
    @Component
    class PostSearchDaoImpl implements PostSearchDao {

        private final JPAQueryFactory query;
        private final CategoryQueryRepository categoryQueryRepository;

        @Override
        public Page<Post> search(@Nullable Long memberId, PostSearchCond cond, Pageable pageable) {
            JPAQuery<Long> countQuery = query.select(post.countDistinct())
                    .from(post)
                    .where(
                            filterPrivatePost(memberId),
                            blogEq(cond.blogName()),
                            hasCategory(cond.categoryId()),
                            hasTag(cond.tag()),
                            writerIdEq(cond.writerId()),
                            titleOrContentContains(cond.title(), cond.bodyText(), cond.titleOrBodyText())
                    );
            List<Post> result = query.selectFrom(post)
                    .distinct()
                    .leftJoin(post.blog, blog).fetchJoin()
                    .leftJoin(post.content.tags, tag)
                    .join(post.content.writer, member).fetchJoin()
                    .leftJoin(post.content.category, category).fetchJoin()
                    .where(
                            filterPrivatePost(memberId),
                            blogEq(cond.blogName()),
                            hasCategory(cond.categoryId()),
                            hasTag(cond.tag()),
                            writerIdEq(cond.writerId()),
                            titleOrContentContains(cond.title(), cond.bodyText(), cond.titleOrBodyText())
                    )
                    .orderBy(post.createdDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return getPage(result, pageable, countQuery::fetchOne);
        }

        private BooleanExpression filterPrivatePost(@Nullable Long memberId) {
            if (memberId == null) {
                return post.visibilityPolish.visibility.ne(PRIVATE);
            }
            return post.visibilityPolish.visibility.ne(PRIVATE)
                    .or(post.visibilityPolish.visibility.eq(PRIVATE)
                            .and(post.content.writer.id.eq(memberId)));
        }

        private BooleanExpression blogEq(@Nullable String blogName) {
            if (ObjectUtils.isEmpty(blogName)) {
                return null;
            }
            return post.blog.name.value.eq(blogName);
        }

        private BooleanExpression hasCategory(@Nullable Long categoryId) {
            if (categoryId == null) {
                return null;
            }
            List<Long> categoryIds = categoryQueryRepository.getCategoryAndDescendants(categoryId);
            return post.content.category.id.in(categoryIds);
        }

        private BooleanExpression hasTag(@Nullable String tagName) {
            if (ObjectUtils.isEmpty(tagName)) {
                return null;
            }
            return tag.content.eq(tagName);
        }

        private BooleanExpression writerIdEq(@Nullable Long writerId) {
            if (writerId == null) {
                return null;
            }
            return post.content.writer.id.eq(writerId);
        }

        private BooleanExpression titleOrContentContains(
                @Nullable String title,
                @Nullable String content,
                @Nullable String titleOrContent
        ) {
            if ((!ObjectUtils.isEmpty(title) || !ObjectUtils.isEmpty(content))
                    && (!ObjectUtils.isEmpty(titleOrContent))) {
                throw new BadPostSearchCondException("제목이나 내용을 검색하는 경우 제목 + 내용으로는 검색할 수 없습니다");
            }
            if (!ObjectUtils.isEmpty(title)) {
                return post.content.title.containsIgnoreCase(title);
            }
            if (!ObjectUtils.isEmpty(content)) {
                return post.content.bodyText.containsIgnoreCase(content);
            }
            if (!ObjectUtils.isEmpty(titleOrContent)) {
                return post.content.title.containsIgnoreCase(titleOrContent)
                        .or(post.content.bodyText.containsIgnoreCase(titleOrContent));
            }
            return null;
        }
    }
}
