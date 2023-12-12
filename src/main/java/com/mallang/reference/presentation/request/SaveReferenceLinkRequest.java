package com.mallang.reference.presentation.request;

import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record SaveReferenceLinkRequest(
        @NotBlank String url,
        @NotBlank String title,
        @Nullable String memo
) {
    public SaveReferenceLinkCommand toCommand(Long memberId) {
        return new SaveReferenceLinkCommand(memberId, url, title, memo);
    }
}
