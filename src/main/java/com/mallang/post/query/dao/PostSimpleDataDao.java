package com.mallang.post.query.dao;

import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import com.mallang.post.query.repository.PostQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PostSimpleDataDao {

    private final PostQueryRepository postQueryRepository;

    public List<PostSimpleData> search(PostSearchCond cond) {
        return postQueryRepository.search(cond).stream()
                .map(PostSimpleData::from)
                .toList();
    }
}
