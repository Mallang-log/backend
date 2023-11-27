package com.mallang.post.query;

import com.mallang.post.query.dao.PostDetailDao;
import com.mallang.post.query.dao.PostSearchDao;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryService {

    private final PostDetailDao postDetailDao;
    private final PostSearchDao postSearchDao;
    private final PostDataValidator postDataValidator;
    private final PostDataProtector postDataProtector;

    public PostDetailResponse getByIdAndBlogName(Long id,
                                                 String blogName,
                                                 @Nullable Long memberId,
                                                 @Nullable String postPassword) {
        PostDetailResponse postDetailResponse = postDetailDao.find(id, blogName, memberId);
        postDataValidator.validateAccessPost(memberId, postDetailResponse);
        return postDataProtector.protectIfRequired(memberId, postPassword, postDetailResponse);
    }

    public Page<PostSearchResponse> search(PostSearchCond cond, Pageable pageable, @Nullable Long memberId) {
        Page<PostSearchResponse> result = postSearchDao.search(memberId, cond, pageable);
        return postDataProtector.protectIfRequired(memberId, result);
    }
}

