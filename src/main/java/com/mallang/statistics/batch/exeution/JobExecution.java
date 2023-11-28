package com.mallang.statistics.batch.exeution;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class JobExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime executionTime;

    private Status status;

    private String whyFail;

    public JobExecution(String name, LocalDateTime executionTime) {
        this.name = name;
        this.executionTime = executionTime;
        this.status = Status.PROCESSING;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void fail(String whyFail) {
        status = Status.FAIL;
        this.whyFail = whyFail;
    }
}
