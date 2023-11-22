package com.mallang.post.query.dao;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QTag.tag;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static org.springframework.data.support.PageableExecutionUtils.getPage;

import com.mallang.category.query.support.CategoryQuerySupport;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.query.response.PostSearchResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostSearchDao {

    private final JPAQueryFactory query;
    private final CategoryQuerySupport categoryQuerySupport;

    public Page<PostSearchResponse> search(@Nullable Long memberId, PostSearchCond cond, Pageable pageable) {
        JPAQuery<Long> countQuery = query.select(post.countDistinct())
                .leftJoin(post.tags, tag)
                .where(
                        filterPrivatePost(memberId),
                        blogEq(cond.blogName()),
                        hasCategory(cond.categoryId()),
                        hasTag(cond.tag()),
                        writerIdEq(cond.writerId()),
                        titleOrContentContains(cond.title(), cond.content(), cond.titleOrContent())
                );
        List<Post> result = query.selectFrom(post)
                .distinct()
                .leftJoin(post.tags, tag)
                .where(
                        filterPrivatePost(memberId),
                        blogEq(cond.blogName()),
                        hasCategory(cond.categoryId()),
                        hasTag(cond.tag()),
                        writerIdEq(cond.writerId()),
                        titleOrContentContains(cond.title(), cond.content(), cond.titleOrContent())
                )
                .orderBy(post.id.desc())
                .fetch();
        return getPage(result, pageable, countQuery::fetchOne)
                .map(PostSearchResponse::from);
    }

    private BooleanExpression filterPrivatePost(@Nullable Long memberId) {
        if (memberId == null) {
            return post.visibilityPolish.visibility.ne(PRIVATE);
        }
        return post.visibilityPolish.visibility.ne(PRIVATE)
                .or(post.visibilityPolish.visibility.eq(PRIVATE)
                        .and(post.writer.id.eq(memberId)));
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
        List<Long> categoryIds = categoryQuerySupport.getCategoryAndDescendants(categoryId);
        return post.category.id.in(categoryIds);
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
        return post.writer.id.eq(writerId);
    }

    private BooleanExpression titleOrContentContains(@Nullable String title, @Nullable String content,
                                                     @Nullable String titleOrContent) {
        if ((!ObjectUtils.isEmpty(title) || !ObjectUtils.isEmpty(content))
                && (!ObjectUtils.isEmpty(titleOrContent))) {
            throw new BadPostSearchCondException("제목이나 내용을 검색하는 경우 제목 + 내용으로는 검색할 수 없습니다");
        }
        if (!ObjectUtils.isEmpty(title)) {
            return post.title.containsIgnoreCase(title);
        }
        if (!ObjectUtils.isEmpty(content)) {
            return post.content.containsIgnoreCase(content);
        }
        if (!ObjectUtils.isEmpty(titleOrContent)) {
            return post.title.containsIgnoreCase(titleOrContent)
                    .or(post.content.containsIgnoreCase(titleOrContent));
        }
        return null;
    }

    @Builder
    public record PostSearchCond(
            @Nullable String blogName,
            @Nullable Long writerId,
            @Nullable Long categoryId,
            @Nullable String tag,
            @Nullable String title,
            @Nullable String content,
            @Nullable String titleOrContent
    ) {
    }
}
