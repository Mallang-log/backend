package com.mallang.post.query;


import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.query.repository.DraftQueryRepository;
import com.mallang.post.query.response.DraftDetailResponse;
import com.mallang.post.query.response.DraftListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DraftQueryService {

    private final BlogQueryRepository blogQueryRepository;
    private final DraftQueryRepository draftQueryRepository;
    private final MemberQueryRepository memberQueryRepository;

    public List<DraftListResponse> findAllByBlog(Long memberId, String blogName) {
        Member member = memberQueryRepository.getById(memberId);
        Blog blog = blogQueryRepository.getByName(blogName);
        blog.validateOwner(member);
        return draftQueryRepository.findAllByBlogOrderByUpdatedDateDesc(blog)
                .stream()
                .map(DraftListResponse::from)
                .toList();
    }

    public DraftDetailResponse findById(Long memberId, Long draftId) {
        Member member = memberQueryRepository.getById(memberId);
        Draft draft = draftQueryRepository.getById(draftId);
        draft.validateWriter(member);
        return DraftDetailResponse.from(draft);
    }
}
