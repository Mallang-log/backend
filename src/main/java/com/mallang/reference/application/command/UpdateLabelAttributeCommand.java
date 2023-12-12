package com.mallang.reference.application.command;

public record UpdateLabelAttributeCommand(
        Long memberId,
        Long labelId,
        String name,
        String colorCode
) {
}
