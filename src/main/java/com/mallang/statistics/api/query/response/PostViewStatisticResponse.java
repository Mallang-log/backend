package com.mallang.statistics.api.query.response;

import com.mallang.statistics.api.query.support.PeriodPartitioner.PeriodPart;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PostViewStatisticResponse implements CommonStatisticResponse {

    private LocalDate startDateInclude;
    private LocalDate endDateInclude;
    private int viewCount;

    public static PostViewStatisticResponse from(PeriodPart periodPart) {
        return new PostViewStatisticResponse(periodPart.startInclude(), periodPart.endInclude());
    }

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

