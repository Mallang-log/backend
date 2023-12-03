package com.mallang.reference.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.reference.query.repository.ReferenceLinkQueryRepository;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoCond;
import com.mallang.reference.query.response.ReferenceLinkSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReferenceLinkQueryService {

    private final BlogQueryRepository blogQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final ReferenceLinkQueryRepository referenceLinkQueryRepository;

    public boolean existsReferenceLinkByUrl(Long memberId, String blogName, String url) {
        Blog blog = blogQueryRepository.getByName(blogName);
        Member member = memberQueryRepository.getById(memberId);
        blog.validateOwner(member);
        return referenceLinkQueryRepository.existsByBlogAndUrl(blog, url);
    }

    public List<ReferenceLinkSearchResponse> search(Long memberId, String blogName, ReferenceLinkSearchDaoCond cond) {
        Blog blog = blogQueryRepository.getByName(blogName);
        Member member = memberQueryRepository.getById(memberId);
        blog.validateOwner(member);
        return ReferenceLinkSearchResponse.from(referenceLinkQueryRepository.search(blog, cond));
    }
}
