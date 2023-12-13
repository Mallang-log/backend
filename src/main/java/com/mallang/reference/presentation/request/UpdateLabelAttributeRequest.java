package com.mallang.reference.presentation.request;

import com.mallang.reference.application.command.UpdateLabelAttributeCommand;

public record UpdateLabelAttributeRequest(
        String name,
        String colorCode
) {
    public UpdateLabelAttributeCommand toCommand(Long labelId, Long memberId) {
        return new UpdateLabelAttributeCommand(
                memberId,
                labelId,
                name,
                colorCode
        );
    }
}
