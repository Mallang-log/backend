package com.mallang.post.query;

import com.mallang.post.query.dao.PostManageDetailDataDao;
import com.mallang.post.query.dao.PostManageSimpleDataDao;
import com.mallang.post.query.data.PostManageDetailData;
import com.mallang.post.query.data.PostManageSearchCond;
import com.mallang.post.query.data.PostManageSimpleData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostManageQueryService {

    private final PostManageDetailDataDao postManageDetailDataDao;
    private final PostManageSimpleDataDao postManageSimpleDataDao;

    public PostManageDetailData findById(Long memberId, Long id) {
        return postManageDetailDataDao.find(memberId, id);
    }

    public List<PostManageSimpleData> search(Long memberId, PostManageSearchCond cond) {
        return postManageSimpleDataDao.search(memberId, cond);
    }
}
