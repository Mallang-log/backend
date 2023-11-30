package com.mallang.post.query.response;

import com.mallang.post.domain.draft.Draft;
import java.time.LocalDateTime;

public record DraftListResponse(
        Long draftId,
        String title,
        LocalDateTime updatedDate
) {
    public static DraftListResponse from(Draft draft) {
        return new DraftListResponse(
                draft.getId(),
                draft.getTitle(),
                draft.getUpdatedDate()
        );
    }
}
