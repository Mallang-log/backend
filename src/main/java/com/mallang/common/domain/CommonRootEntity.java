package com.mallang.common.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor(access = PROTECTED)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CommonRootEntity<ID> extends AbstractAggregateRoot<CommonRootEntity<ID>> {

    public abstract ID getId();

    @CreatedDate
    private LocalDateTime createdDate;

    @Override
    public List<Object> domainEvents() {
        return new ArrayList<>(super.domainEvents());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonRootEntity<?> that)) {
            return false;
        }
        if (getId() == null || that.getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
