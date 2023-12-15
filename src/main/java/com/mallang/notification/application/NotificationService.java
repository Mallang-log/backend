package com.mallang.notification.application;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.notification.domain.Notification;
import com.mallang.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public void read(Long memberId, Long notificationId) {
        Member member = memberRepository.getById(memberId);
        Notification notification = notificationRepository.getById(notificationId);
        notification.validateMember(member);
        notification.read();
    }

    public void delete(Long memberId, Long notificationId) {
        Member member = memberRepository.getById(memberId);
        Notification notification = notificationRepository.getById(notificationId);
        notification.validateMember(member);
        notificationRepository.delete(notification);
    }
}
