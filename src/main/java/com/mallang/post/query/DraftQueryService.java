package com.mallang.post.query;


import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import com.mallang.post.query.dao.DraftListDao;
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

    private final MemberRepository memberRepository;
    private final DraftRepository draftRepository;
    private final BlogRepository blogRepository;

    private final DraftListDao draftListDao;

    public List<DraftListResponse> findAllByBlog(Long memberId, String blogName) {
        Member member = memberRepository.getById(memberId);
        Blog blog = blogRepository.getByName(blogName);
        blog.validateOwner(member);
        return draftListDao.find(blog);
    }

    public DraftDetailResponse findById(Long memberId, Long draftId) {
        Member member = memberRepository.getById(memberId);
        Draft draft = draftRepository.getById(draftId);
        draft.validateWriter(member);
        return DraftDetailResponse.from(draft);
    }
}
