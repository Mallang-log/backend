package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("AnonymousWriter")
@Entity
public class AnonymousWriter extends CommentWriter {

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;
}
