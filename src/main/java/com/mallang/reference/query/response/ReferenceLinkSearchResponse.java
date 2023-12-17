package com.mallang.reference.query.response;

import com.mallang.reference.domain.Label;
import com.mallang.reference.domain.ReferenceLink;
import java.util.List;

public record ReferenceLinkSearchResponse(
        Long referenceLinkId,
        String url,
        String title,
        String memo,
        LabelResponse label
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
                referenceLink.getMemo(),
                LabelResponse.from(referenceLink.getLabel())
        );
    }

    public record LabelResponse(
            Long id,
            String name,
            String color
    ) {
        public static LabelResponse from(Label label) {
            if (label == null) {
                return new LabelResponse(null, null, null);
            }
            return new LabelResponse(
                    label.getId(),
                    label.getName(),
                    label.getColor()
            );
        }
    }
}
