package com.mallang.auth.domain;

import com.mallang.auth.exception.DuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BasicMemberValidator {

    private final BasicMemberRepository basicMemberRepository;

    public void validateDuplicateUsername(String username) {
        if (basicMemberRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException();
        }
    }
}
