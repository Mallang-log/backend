package com.mallang.post.query;

import com.mallang.post.query.dao.StaredPostDao;
import com.mallang.post.query.response.StaredPostResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostStarQueryService {

    private final StaredPostDao staredPostDao;
    private final PostDataProtector postDataProtector;

    public Page<StaredPostResponse> findAllByMemberId(
            Long targetMemberId,
            @Nullable Long requesterId,
            Pageable pageable
    ) {
        Page<StaredPostResponse> staredPostData = staredPostDao.find(targetMemberId, pageable);
        return postDataProtector.protectStaredIfRequired(requesterId, staredPostData);
    }
}

