package com.mallang.notification.presentation;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mallang.auth.presentation.support.Auth;
import com.mallang.common.presentation.PageResponse;
import com.mallang.notification.application.NotificationService;
import com.mallang.notification.query.NotificationQueryService;
import com.mallang.notification.query.response.NotificationListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationQueryService notificationQueryService;

    @PostMapping("/read/{id}")
    public ResponseEntity<Void> read(
            @Auth Long memberId,
            @PathVariable("id") Long id
    ) {
        notificationService.read(memberId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Auth Long memberId,
            @PathVariable("id") Long id
    ) {
        notificationService.delete(memberId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<NotificationListResponse>> findAllByMember(
            @Auth Long memberId,
            @PageableDefault(size = 30, sort = "createdDate", direction = DESC) Pageable pageable
    ) {
        Page<NotificationListResponse> result = notificationQueryService.findAllByMemberId(memberId, pageable);
        PageResponse<NotificationListResponse> response = PageResponse.from(result);
        return ResponseEntity.ok(response);
    }
}
