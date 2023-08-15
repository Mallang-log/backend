package com.mallang.post.application;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSearchCond;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;

    public PostDetailResponse getById(Long id) {
        return PostDetailResponse.from(postRepository.getById(id));
    }

    public List<PostSimpleResponse> search(PostSearchCond cond) {
        return postRepository.findAll()
                .stream()
                .filter(it -> categoryFilter(cond, it))
                .map(PostSimpleResponse::from)
                .toList();
    }

    private boolean categoryFilter(PostSearchCond cond, Post post) {
        if (Objects.isNull(cond.categoryId()) || cond.categoryId() == 0) {
            return true;
        }
        if (Objects.isNull(post.getCategory())) {
            return false;
        }
        return post.getCategory().equalIdOrContainsIdInParent(cond.categoryId());
    }
}

