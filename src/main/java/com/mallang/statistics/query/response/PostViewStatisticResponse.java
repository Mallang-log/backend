package com.mallang.statistics.query.response;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PostViewStatisticResponse {

    private LocalDate startDateInclude;
    private LocalDate endDateInclude;
    private int viewCount;

    public PostViewStatisticResponse(LocalDate startDateInclude, LocalDate endDateInclude, int viewCount) {
        this.startDateInclude = startDateInclude;
        this.endDateInclude = endDateInclude;
        this.viewCount = viewCount;
    }

    public PostViewStatisticResponse(LocalDate startDateInclude, LocalDate endDateInclude) {
        this.startDateInclude = startDateInclude;
        this.endDateInclude = endDateInclude;
    }

    public void addViewCount(int count) {
        this.viewCount += count;
    }
}

