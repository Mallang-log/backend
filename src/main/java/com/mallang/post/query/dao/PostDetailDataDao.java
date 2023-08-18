package com.mallang.post.query.dao;

import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostDetailDataDao {

    private final PostQueryRepository postQueryRepository;

    public PostDetailData find(Long id) {
        return PostDetailData.from(postQueryRepository.getById(id));
    }
}
