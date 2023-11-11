package com.mallang.post.query;

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
    private final PostDataValidator postDataValidator;
    private final PostDataProtector postDataProtector;

    public PostDetailData getById(@Nullable Long memberId, Long id) {
        PostDetailData postDetailData = postDetailDataDao.find(memberId, id);
        postDataValidator.validateAccessPost(memberId, postDetailData);
        return postDataProtector.protectIfRequired(memberId, postDetailData);
    }

    public PostDetailData getProtectedById(@Nullable Long memberId, Long id, String password) {
        PostDetailData postDetailData = postDetailDataDao.find(memberId, id);
        postDataValidator.validateAccessProtectedPost(postDetailData, password);
        return postDetailData;
    }

    public List<PostSimpleData> search(@Nullable Long memberId, PostSearchCond cond) {
        List<PostSimpleData> result = postSimpleDataDao.search(memberId, cond);
        return postDataProtector.protectIfRequired(memberId, result);
    }
}

