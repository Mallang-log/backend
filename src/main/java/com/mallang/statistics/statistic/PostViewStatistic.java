package com.mallang.statistics.statistic;

import com.mallang.post.domain.PostId;
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
public class PostViewStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDate statisticDate;

    @Column(nullable = false, updatable = false)
    private PostId postId;

    @Column
    private int count;

    public PostViewStatistic(LocalDate statisticDate, PostId postId) {
        this(statisticDate, postId, 0);
    }

    public PostViewStatistic(LocalDate statisticDate, PostId postId, int count) {
        this.statisticDate = statisticDate;
        this.postId = postId;
        this.count = count;
    }

    public void addCount(int amount) {
        this.count += amount;
    }
}
