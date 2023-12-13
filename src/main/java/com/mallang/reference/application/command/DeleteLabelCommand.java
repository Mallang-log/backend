package com.mallang.reference.application.command;

public record DeleteLabelCommand(
        Long memberId,
        Long labelId
) {
}
