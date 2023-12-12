package com.mallang.reference.query;

import com.mallang.reference.domain.Label;
import com.mallang.reference.query.repository.LabelQueryRepository;
import com.mallang.reference.query.response.LabelListResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LabelQueryService {

    private final LabelQueryRepository labelQueryRepository;

    public List<LabelListResponse> findAllByMemberId(Long memberId) {
        List<Label> labels = labelQueryRepository.findAllByOwnerId(memberId);
        if (labels.isEmpty()) {
            return Collections.emptyList();
        }
        Label firstLabel = labels.stream()
                .filter(it -> it.getPreviousSibling() == null)
                .findAny()
                .orElseThrow();
        List<Label> siblings = firstLabel.getSiblingsExceptSelf();
        siblings.addFirst(firstLabel);
        return siblings.stream()
                .map(LabelListResponse::from)
                .toList();
    }
}
