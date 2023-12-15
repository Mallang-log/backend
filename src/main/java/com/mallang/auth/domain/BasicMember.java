package com.mallang.auth.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("basic")
@Entity
public class BasicMember extends Member {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private Password password;

    public BasicMember(
            String username,
            Password password,
            String nickname,
            String profileImageUrl
    ) {
        super(nickname, profileImageUrl);
        this.username = username;
        this.password = password;
    }

    public void signup(BasicMemberValidator validator) {
        validator.validateDuplicateUsername(username);
    }

    public void login(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password.validatePassword(rawPassword, passwordEncoder);
    }
}
