package com.mallang.reference.presentation.request;

import com.mallang.reference.application.command.UpdateReferenceLinkCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record UpdateReferenceLinkRequest(
        @NotBlank String url,
        @NotBlank String title,
        @Nullable String memo
) {
    public UpdateReferenceLinkCommand toCommand(Long referenceLinkId, Long memberId) {
        return new UpdateReferenceLinkCommand(
                referenceLinkId,
                memberId,
                url,
                title,
                memo
        );
    }
}
