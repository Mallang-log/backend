package com.mallang.reference.query.response;

import com.mallang.reference.domain.Label;

public record LabelListResponse(
        Long id,
        String name,
        String color,
        Long prevLabelId,
        Long nextLabelId
) {

    public static LabelListResponse from(Label label) {
        Label prev = label.getPreviousSibling();
        Label next = label.getNextSibling();
        return new LabelListResponse(
                label.getId(),
                label.getName(),
                label.getColor(),
                prev == null ? null : prev.getId(),
                next == null ? null : next.getId()
        );
    }
}
