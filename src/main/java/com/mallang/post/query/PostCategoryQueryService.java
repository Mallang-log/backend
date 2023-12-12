package com.mallang.post.query;

import com.mallang.post.domain.PostCategory;
import com.mallang.post.query.repository.PostCategoryQueryRepository;
import com.mallang.post.query.response.PostCategoryResponse;
import java.util.Collections;
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
        List<PostCategory> categories = postCategoryQueryRepository.findAllByBlogName(blogName);
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }
        PostCategory firstRoot = getFirstRoot(categories);
        List<PostCategory> roots = firstRoot.getSiblingsExceptSelf();
        roots.addFirst(firstRoot);
        return roots.stream()
                .map(PostCategoryResponse::from)
                .toList();
    }

    private PostCategory getFirstRoot(List<PostCategory> all) {
        return all.stream()
                .filter(it -> it.getParent() == null)
                .filter(it -> it.getPreviousSibling() == null)
                .findAny()
                .orElseThrow();
    }
}
