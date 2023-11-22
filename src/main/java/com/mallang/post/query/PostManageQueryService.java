package com.mallang.post.query;

import com.mallang.post.query.dao.PostManageDetailDao;
import com.mallang.post.query.dao.PostManageSearchDao;
import com.mallang.post.query.dao.PostManageSearchDao.PostManageSearchCond;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostManageQueryService {

    private final PostManageDetailDao postManageDetailDao;
    private final PostManageSearchDao postManageSearchDao;

    public PostManageDetailResponse findById(Long memberId, Long id) {
        return postManageDetailDao.find(memberId, id);
    }

    public List<PostManageSearchResponse> search(Long memberId, PostManageSearchCond cond) {
        return postManageSearchDao.search(memberId, cond);
    }
}
