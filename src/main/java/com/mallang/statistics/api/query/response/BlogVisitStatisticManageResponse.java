package com.mallang.statistics.api.query.response;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class BlogVisitStatisticManageResponse {

    private LocalDate startDateInclude;
    private LocalDate endDateInclude;
    private int visitCount;

    public BlogVisitStatisticManageResponse(LocalDate startDateInclude, LocalDate endDateInclude) {
        this(startDateInclude, endDateInclude, 0);
    }

    public BlogVisitStatisticManageResponse(LocalDate startDateInclude, LocalDate endDateInclude, int visitCount) {
        this.startDateInclude = startDateInclude;
        this.endDateInclude = endDateInclude;
        this.visitCount = visitCount;
    }

    public void addVisitCount(int count) {
        this.visitCount += count;
    }
}

