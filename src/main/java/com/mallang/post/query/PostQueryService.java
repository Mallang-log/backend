package com.mallang.post.query;

import com.mallang.post.query.dao.PostDetailDao;
import com.mallang.post.query.dao.PostSearchDao;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    public PostDetailResponse getById(@Nullable Long memberId, @Nullable String postPassword, Long id) {
        PostDetailResponse postDetailResponse = postDetailDao.find(memberId, id);
        postDataValidator.validateAccessPost(memberId, postDetailResponse);
        return postDataProtector.protectIfRequired(memberId, postPassword, postDetailResponse);
    }

    public List<PostSearchResponse> search(@Nullable Long memberId, PostSearchCond cond) {
        List<PostSearchResponse> result = postSearchDao.search(memberId, cond);
        return postDataProtector.protectIfRequired(memberId, result);
    }
}

