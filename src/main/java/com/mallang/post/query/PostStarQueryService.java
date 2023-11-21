package com.mallang.post.query;

import com.mallang.post.query.dao.StaredPostDao;
import com.mallang.post.query.response.StaredPostResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostStarQueryService {

    private final StaredPostDao staredPostDao;
    private final PostDataProtector postDataProtector;

    public List<StaredPostResponse> findAllByMemberId(Long targetMemberId, @Nullable Long requesterId) {
        List<StaredPostResponse> staredPostData = staredPostDao.find(targetMemberId);
        return postDataProtector.protectStaredIfRequired(requesterId, staredPostData);
    }
}

