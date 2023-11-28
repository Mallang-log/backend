package com.mallang.statistics.api.query.response;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PostViewStatisticResponse {

    private LocalDate startDateInclude;
    private LocalDate endDateInclude;
    private int viewCount;

    public PostViewStatisticResponse() {
        this(null, null);
    }

    public PostViewStatisticResponse(LocalDate startDateInclude, LocalDate endDateInclude) {
        this(startDateInclude, endDateInclude, 0);
    }

    public PostViewStatisticResponse(LocalDate startDateInclude, LocalDate endDateInclude, int viewCount) {
        this.startDateInclude = startDateInclude;
        this.endDateInclude = endDateInclude;
        this.viewCount = viewCount;
    }

    public void addViewCount(int count) {
        this.viewCount += count;
    }
}

