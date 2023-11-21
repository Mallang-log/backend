package com.mallang.post.query.dao;

import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.support.PostQuerySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostManageDetailDao {

    private final PostQuerySupport postQuerySupport;

    public PostManageDetailResponse find(Long memberId, Long id) {
        return PostManageDetailResponse.from(postQuerySupport.getByIdAndWriterId(id, memberId));
    }
}
