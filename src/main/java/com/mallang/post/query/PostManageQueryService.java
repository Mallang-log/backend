package com.mallang.post.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.query.repository.BlogQueryRepository;
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

    private final BlogQueryRepository blogQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberQueryRepository memberQueryRepository;

    public PostManageDetailResponse getById(Long memberId, Long postId, String blogName) {
        Member member = memberQueryRepository.getById(memberId);
        Post post = postQueryRepository.getById(postId, blogName);
        post.validateWriter(member);
        return PostManageDetailResponse.from(post);
    }

    public Page<PostManageSearchResponse> search(
            Long memberId,
            String blogName,
            PostManageSearchCond cond,
            Pageable pageable
    ) {
        Blog blog = blogQueryRepository.getByName(blogName);
        Member member = memberQueryRepository.getById(memberId);
        blog.validateOwner(member);
        return postQueryRepository.searchForManage(blog, cond, pageable)
                .map(PostManageSearchResponse::from);
    }
}
