package com.mallang.statistics.api.query.response;

import com.mallang.statistics.api.query.support.PeriodPartitioner.PeriodPart;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class BlogVisitStatisticManageResponse implements CommonStatisticResponse {

    private LocalDate startDateInclude;
    private LocalDate endDateInclude;
    private int visitCount;

    public static BlogVisitStatisticManageResponse from(PeriodPart periodPart) {
        return new BlogVisitStatisticManageResponse(periodPart.startInclude(), periodPart.endInclude());
    }

    public BlogVisitStatisticManageResponse() {
    }

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

