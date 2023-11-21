package com.mallang.post.query;

import com.mallang.post.query.dao.StaredPostDataDao;
import com.mallang.post.query.data.StaredPostData;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostStarQueryService {

    private final StaredPostDataDao staredPostDataDao;
    private final PostDataProtector postDataProtector;

    public List<StaredPostData> findAllByMemberId(Long targetMemberId, @Nullable Long requesterId) {
        List<StaredPostData> staredPostData = staredPostDataDao.find(targetMemberId);
        return postDataProtector.protectStaredIfRequired(requesterId, staredPostData);
    }
}

