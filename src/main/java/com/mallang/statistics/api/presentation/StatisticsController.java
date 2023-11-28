package com.mallang.statistics.api.presentation;

import com.mallang.statistics.api.query.StatisticQueryService;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/statistics")
@RestController
public class StatisticsController {

    private final StatisticQueryService statisticQueryService;

    @GetMapping("/blogs/{blogName}")
    public ResponseEntity<BlogVisitStatisticSimpleResponse> getBlogVisitStatistics(
            @PathVariable("blogName") String blogName,
            @RequestParam("today") LocalDate localDate
    ) {
        return ResponseEntity.ok(
                statisticQueryService.getSimpleBlogVisitStatistics(blogName, localDate)
        );
    }
}
