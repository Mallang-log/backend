package com.mallang.statistics.api.presentation;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.statistics.api.presentation.request.StatisticConditionRequest;
import com.mallang.statistics.api.query.StatisticQueryService;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/manage/statistics")
@RestController
public class StatisticsManageController {

    private final StatisticQueryService statisticQueryService;

    @GetMapping("/posts/{blogName}/{id}")
    public ResponseEntity<List<PostViewStatisticResponse>> getPostViewStatistics(
            @Auth Long memberId,
            @PathVariable("blogName") String blogName,
            @PathVariable("id") Long id,
            @ModelAttribute StatisticConditionRequest request
    ) {
        return ResponseEntity.ok(
                statisticQueryService.getPostStatistics(request.toDto(memberId, blogName, id))
        );
    }
}
