package com.mallang.post.query.dao;

import com.mallang.post.query.dao.support.PostQuerySupport;
import com.mallang.post.query.data.PostManageDetailData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostManageDetailDataDao {

    private final PostQuerySupport postQuerySupport;

    public PostManageDetailData find(Long memberId, Long id) {
        return PostManageDetailData.from(postQuerySupport.getByIdAndWriterId(id, memberId));
    }
}
