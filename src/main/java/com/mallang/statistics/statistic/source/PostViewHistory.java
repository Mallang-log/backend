package com.mallang.statistics.statistic.source;

import com.mallang.post.domain.PostId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostViewHistory extends CommonHistory {

    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private PostId postId;

    private LocalDate date;

    public PostViewHistory(UUID uuid, PostId postId, LocalDateTime createdDate) {
        super(createdDate);
        this.uuid = uuid;
        this.postId = postId;
        this.date = createdDate.toLocalDate();
    }
}
