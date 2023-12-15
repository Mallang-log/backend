package com.mallang.notification.query;

import com.mallang.notification.domain.Notification;
import com.mallang.notification.query.mapper.NotificationResponseMapperComposite;
import com.mallang.notification.query.repository.NotificationQueryRepository;
import com.mallang.notification.query.response.NotificationListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;
    private final NotificationResponseMapperComposite mapper;

    public Page<NotificationListResponse> findAllByMemberId(Long memberId, Pageable pageable) {
        Page<Notification> result =
                notificationQueryRepository.findAllByTargetMemberIdOrderByCreatedDateDesc(memberId, pageable);
        return result.map(mapper::mapToResponse);
    }
}
