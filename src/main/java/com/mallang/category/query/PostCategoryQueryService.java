package com.mallang.category.query;

import com.mallang.category.query.repository.PostCategoryQueryRepository;
import com.mallang.category.query.response.PostCategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostCategoryQueryService {

    private final PostCategoryQueryRepository postCategoryQueryRepository;

    public List<PostCategoryResponse> findAllByBlogName(String blogName) {
        return postCategoryQueryRepository.findAllRootByBlogName(blogName)
                .stream()
                .map(PostCategoryResponse::from)
                .toList();
    }
}
