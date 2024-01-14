package com.mallang.statistics.statistic.source;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BlogVisitHistory extends CommonHistory {

    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String blogName;

    private String origin;
    private String ip;

    private LocalDate date;

    @Builder
    public BlogVisitHistory(UUID uuid, String blogName, String origin, String ip, LocalDateTime createdDate) {
        super(createdDate);
        this.uuid = uuid;
        this.blogName = blogName;
        this.origin = origin;
        this.ip = ip;
        this.date = createdDate.toLocalDate();
    }
}
