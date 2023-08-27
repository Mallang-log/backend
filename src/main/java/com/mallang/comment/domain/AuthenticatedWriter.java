package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class AuthenticatedWriter extends CommentWriter {

    @Column(nullable = false)
    private Long memberId;
}
