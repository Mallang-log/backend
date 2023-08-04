package com.mallang.post.application;

import com.mallang.post.application.query.PostDetailResponse;
import com.mallang.post.application.query.PostSimpleResponse;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;

    public PostDetailResponse getById(Long id) {
        return PostDetailResponse.from(postRepository.getById(id));
    }

    public List<PostSimpleResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostSimpleResponse::from)
                .toList();
    }
}

