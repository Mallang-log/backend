package com.mallang.statistics.batch.job;

import java.time.LocalDate;

public record PostViewHistoryDto(
        Long postId,
        Long blogId,
        LocalDate date,
        int viewCount
) {
}
