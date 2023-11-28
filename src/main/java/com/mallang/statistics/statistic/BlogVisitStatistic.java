package com.mallang.statistics.statistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BlogVisitStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDate statisticDate;

    @Column(nullable = false, updatable = false)
    private String blogName;

    @Column
    private int count;

    public BlogVisitStatistic(LocalDate statisticDate, String blogName) {
        this(statisticDate, blogName, 0);
    }

    public BlogVisitStatistic(LocalDate statisticDate, String blogName, int count) {
        this.statisticDate = statisticDate;
        this.blogName = blogName;
        this.count = count;
    }

    public void addCount(int amount) {
        this.count += amount;
    }
}
