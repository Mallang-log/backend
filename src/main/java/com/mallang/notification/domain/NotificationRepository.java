package com.mallang.notification.domain;

import com.mallang.notification.exception.NotFoundNotificationException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    default Notification getById(Long id) {
        return findById(id).orElseThrow(NotFoundNotificationException::new);
    }
}
