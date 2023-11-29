package com.mallang.auth.application;

import com.mallang.auth.application.command.BasicSignupCommand;
import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.domain.BasicMemberRepository;
import com.mallang.auth.domain.BasicMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService {

    private final BasicMemberRepository basicMemberRepository;
    private final BasicMemberValidator validator;

    public Long signup(BasicSignupCommand command) {
        BasicMember member = command.toMember();
        member.signup(validator);
        return basicMemberRepository.save(member).getId();
    }
}
