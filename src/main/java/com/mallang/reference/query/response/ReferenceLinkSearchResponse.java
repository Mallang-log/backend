package com.mallang.reference.query.response;

import com.mallang.reference.domain.ReferenceLink;
import java.util.List;

public record ReferenceLinkSearchResponse(
        Long referenceLinkId,
        String url,
        String title,
        String memo
) {
    public static List<ReferenceLinkSearchResponse> from(List<ReferenceLink> referenceLinks) {
        return referenceLinks.stream()
                .map(ReferenceLinkSearchResponse::from)
                .toList();
    }

    public static ReferenceLinkSearchResponse from(ReferenceLink referenceLink) {
        return new ReferenceLinkSearchResponse(
                referenceLink.getId(),
                referenceLink.getUrl(),
                referenceLink.getTitle(),
                referenceLink.getMemo()
        );
    }
}
