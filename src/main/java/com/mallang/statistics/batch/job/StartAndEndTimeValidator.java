package com.mallang.statistics.batch.job;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class StartAndEndTimeValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        Objects.requireNonNull(parameters, "blogVisitStatisticJob에 대한 parameter가 없습니다");
        LocalDateTime startInclude = parameters.getLocalDateTime("startInclude");
        LocalDateTime endExclude = parameters.getLocalDateTime("endExclude");
        Objects.requireNonNull(startInclude, "시작 시간(startInclude)이 존재하지 않습니다.");
        Objects.requireNonNull(endExclude, "끝 시간(endExclude)이 존재하지 않습니다.");
        if (startInclude.isAfter(endExclude) || startInclude.isEqual(endExclude)) {
            throw new JobParametersInvalidException("시작 시간이 끝 시작보다 크거나 같습니다.");
        }
    }
}
