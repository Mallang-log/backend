package com.mallang.post.query;

import com.mallang.post.domain.Post;
import com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond;
import com.mallang.post.query.repository.PostQueryRepository;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostManageQueryService {

    private final PostQueryRepository postQueryRepository;

    public PostManageDetailResponse findById(Long memberId, Long id, String blogName) {
        Post post = postQueryRepository.getByPostIdAndBlogNameAndWriterId(id, blogName, memberId);
        return PostManageDetailResponse.from(post);
    }

    public Page<PostManageSearchResponse> search(Long memberId, PostManageSearchCond cond, Pageable pageable) {
        return postQueryRepository.searchForManage(memberId, cond, pageable)
                .map(PostManageSearchResponse::from);
    }
}
