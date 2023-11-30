package com.mallang.statistics.api.query.response;

import java.time.LocalDate;


public interface CommonStatisticResponse {

    LocalDate getStartDateInclude();

    LocalDate getEndDateInclude();
}
