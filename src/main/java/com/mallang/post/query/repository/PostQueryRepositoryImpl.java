package com.mallang.post.query.repository;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QTag.tag;

import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.query.data.PostSearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Repository
public class PostQueryRepositoryImpl implements PostQueryDslRepository {

    private final JPAQueryFactory query;
    private final CategoryRepository categoryRepository;

    @Override
    public List<Post> search(PostSearchCond cond) {
        return query.selectFrom(post)
                .leftJoin(post.tags, tag)
                .where(
                        hasCategory(cond.categoryId()),
                        hasTag(cond.tag()),
                        writerIdEq(cond.writerId()),
                        titleAndContent(cond.title(), cond.content(), cond.titleOrContent())
                )
                .fetch();
    }

    private BooleanExpression hasCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryRepository.getById(categoryId);
        List<Long> categoryIds = childCategories(category).stream()
                .map(CommonDomainModel::getId)
                .toList();
        return post.category.id.in(categoryIds);
    }

    private List<Category> childCategories(Category category) {
        List<Category> children = new ArrayList<>();
        children.add(category);
        if (category.getChildren().isEmpty()) {
            return children;
        }
        for (Category child : category.getChildren()) {
            children.addAll(childCategories(child));
        }
        return children;
    }

    private BooleanExpression hasTag(String tagName) {
        if (ObjectUtils.isEmpty(tagName)) {
            return null;
        }
        return tag.content.eq(tagName);
    }

    private BooleanExpression writerIdEq(Long writerId) {
        return writerId == null
                ? null
                : post.member.id.eq(writerId);
    }

    private BooleanExpression titleAndContent(String title, String content, String titleOrContent) {
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
