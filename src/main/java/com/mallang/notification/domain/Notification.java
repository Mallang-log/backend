package com.mallang.notification.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static jakarta.persistence.InheritanceType.JOINED;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.common.domain.CommonRootEntity;
import com.mallang.notification.exception.NoAuthorityNotificationException;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@DiscriminatorColumn
@Inheritance(strategy = JOINED)
@Entity
public abstract class Notification extends CommonRootEntity<Long> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private boolean read;

    private Long targetMemberId;

    protected Notification(Long targetMemberId) {
        this.targetMemberId = targetMemberId;
    }

    public void read() {
        this.read = true;
    }

    public void validateMember(Member member) {
        if (!member.getId().equals(targetMemberId)) {
            throw new NoAuthorityNotificationException();
        }
    }
}
