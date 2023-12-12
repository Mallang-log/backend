package com.mallang.post.query;

import com.mallang.post.domain.star.StarGroup;
import com.mallang.post.query.repository.StarGroupQueryRepository;
import com.mallang.post.query.response.StarGroupListResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class StarGroupQueryService {

    private final StarGroupQueryRepository starGroupQueryRepository;

    public List<StarGroupListResponse> findAllByMember(Long memberId) {
        List<StarGroup> groups = starGroupQueryRepository.findAllByOwnerId(memberId);
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }
        StarGroup firstRoot = getFirstRoot(groups);
        List<StarGroup> roots = firstRoot.getSiblingsExceptSelf();
        roots.addFirst(firstRoot);
        return roots.stream()
                .map(StarGroupListResponse::from)
                .toList();
    }

    private StarGroup getFirstRoot(List<StarGroup> all) {
        return all.stream()
                .filter(it -> it.getParent() == null)
                .filter(it -> it.getPreviousSibling() == null)
                .findAny()
                .orElseThrow();
    }
}
