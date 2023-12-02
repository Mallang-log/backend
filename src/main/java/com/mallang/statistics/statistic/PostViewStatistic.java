package com.mallang.statistics.statistic;

import com.mallang.post.domain.PostId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostViewStatistic extends CommonStatistic {

    @Column(nullable = false, updatable = false)
    private PostId postId;

    @Column
    private int count;

    public PostViewStatistic(LocalDate statisticDate, PostId id) {
        this(statisticDate, id, 0);
    }

    public PostViewStatistic(LocalDate statisticDate, PostId id, int count) {
        super(statisticDate);
        this.postId = id;
        this.count = count;
    }

    public void addCount(int amount) {
        this.count += amount;
    }
}
