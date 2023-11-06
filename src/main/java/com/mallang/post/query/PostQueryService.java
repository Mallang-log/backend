package com.mallang.post.query;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.query.dao.PostDetailDataDao;
import com.mallang.post.query.dao.PostSimpleDataDao;
import com.mallang.post.query.data.PostDetailData;
import com.mallang.post.query.data.PostSearchCond;
import com.mallang.post.query.data.PostSimpleData;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryService {

    private final PostDetailDataDao postDetailDataDao;
    private final PostSimpleDataDao postSimpleDataDao;

    public PostDetailData getByBlogNameAndId(@Nullable Long memberId, BlogName blogName, Long id) {
        return postDetailDataDao.find(memberId, blogName, id);
    }

    public List<PostSimpleData> search(PostSearchCond cond) {
        return postSimpleDataDao.search(cond);
    }
}

