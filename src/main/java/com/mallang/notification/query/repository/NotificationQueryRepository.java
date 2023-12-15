package com.mallang.notification.query.repository;

import com.mallang.notification.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationQueryRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByTargetMemberId(Long memberId, Pageable pageable);
}
