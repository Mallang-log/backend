package com.mallang.post.query.dao;

import com.mallang.category.domain.Category;
import com.mallang.post.domain.Post;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import com.mallang.post.query.repository.PostQueryRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostSimpleDataDao {

    private final PostQueryRepository postQueryRepository;

    public List<PostSimpleData> search(PostSearchCond cond) {
        return postQueryRepository.findAll()
                .stream()
                .filter(it -> categoryFilter(cond.categoryId(), it))
                .map(PostSimpleData::from)
                .toList();
    }

    private boolean categoryFilter(Long categoryId, Post post) {
        if (Objects.isNull(categoryId) || categoryId == 0) {
            return true;
        }
        if (Objects.isNull(post.getCategory())) {
            return false;
        }
        return equalIdOrContainsIdInParent(post.getCategory(), categoryId);
    }

    private boolean equalIdOrContainsIdInParent(Category category, Long categoryId) {
        if (categoryId.equals(category.getId())) {
            return true;
        }
        if (category.getParent() == null) {
            return false;
        }
        return equalIdOrContainsIdInParent(category.getParent(), categoryId);
    }
}
