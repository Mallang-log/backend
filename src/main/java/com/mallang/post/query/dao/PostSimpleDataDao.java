package com.mallang.post.query.dao;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QTag.tag;

import com.mallang.category.domain.Category;
import com.mallang.category.query.dao.support.CategoryQuerySupport;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
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
public class PostSimpleDataDao {

    private final JPAQueryFactory query;
    private final CategoryQuerySupport categoryQuerySupport;

    public List<PostSimpleData> search(PostSearchCond cond) {
        return query.selectFrom(post)
                .leftJoin(post.tags, tag)
                .where(
                        hasCategory(cond.categoryId()),
                        hasTag(cond.tag()),
                        writerIdEq(cond.writerId()),
                        titleOrContentContains(cond.title(), cond.content(), cond.titleOrContent())
                )
                .fetch()
                .stream()
                .map(PostSimpleData::from)
                .toList();
    }

    private BooleanExpression hasCategory(@Nullable Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryQuerySupport.getById(categoryId);
        List<Category> descendants = category.getDescendants();
        descendants.add(category);
        List<Long> categoryIds = descendants.stream()
                .map(CommonDomainModel::getId)
                .toList();
        return post.category.id.in(categoryIds);
    }

    private BooleanExpression hasTag(@Nullable String tagName) {
        if (ObjectUtils.isEmpty(tagName)) {
            return null;
        }
        return tag.content.eq(tagName);
    }

    private BooleanExpression writerIdEq(@Nullable Long writerId) {
        return writerId == null
                ? null
                : post.member.id.eq(writerId);
    }

    private BooleanExpression titleOrContentContains(@Nullable String title, @Nullable String content,
                                                     @Nullable String titleOrContent) {
        if (!ObjectUtils.isEmpty(title) || !ObjectUtils.isEmpty(content)) {
            if (!ObjectUtils.isEmpty(titleOrContent)) {
                throw new BadPostSearchCondException("제목이나 내용을 검색하는 경우 제목 + 내용으로는 검색할 수 없습니다");
            }
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
}
