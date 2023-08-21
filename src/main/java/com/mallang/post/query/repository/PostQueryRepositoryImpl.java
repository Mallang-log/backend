package com.mallang.post.query.repository;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.post.domain.QTag.tag;

import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryRepository;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.domain.Post;
import com.mallang.post.query.data.PostSearchCond;
import com.querydsl.core.types.Predicate;
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
                        hasTag(cond.tag())
                )
                .fetch();
    }

    private Predicate hasCategory(Long categoryId) {
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

    private Predicate hasTag(String tagName) {
        if (ObjectUtils.isEmpty(tagName)) {
            return null;
        }
        return tag.content.eq(tagName);
    }
}
