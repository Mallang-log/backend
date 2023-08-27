package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("AnonymousWriter")
@Entity
public class AnonymousWriter extends CommentWriter {

    private String nickname;

    private String password;
}
