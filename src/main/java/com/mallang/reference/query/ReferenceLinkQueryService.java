package com.mallang.reference.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
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

    private final MemberQueryRepository memberQueryRepository;
    private final ReferenceLinkQueryRepository referenceLinkQueryRepository;

    public boolean existsReferenceLinkByUrl(Long memberId, String url) {
        Member member = memberQueryRepository.getById(memberId);
        return referenceLinkQueryRepository.existsByMemberAndUrl(member, url);
    }

    public List<ReferenceLinkSearchResponse> search(Long memberId, ReferenceLinkSearchDaoCond cond) {
        Member member = memberQueryRepository.getById(memberId);
        return ReferenceLinkSearchResponse.from(referenceLinkQueryRepository.search(member, cond));
    }
}
