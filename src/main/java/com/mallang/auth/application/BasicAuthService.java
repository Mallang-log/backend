package com.mallang.auth.application;

import com.mallang.auth.application.command.BasicSignupCommand;
import com.mallang.auth.domain.BasicMember;
import com.mallang.auth.domain.BasicMemberRepository;
import com.mallang.auth.domain.BasicMemberValidator;
import com.mallang.auth.domain.Password;
import com.mallang.auth.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService {

    private final BasicMemberRepository basicMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BasicMemberValidator validator;

    public Long signup(BasicSignupCommand command) {
        Password password = passwordEncoder.encode(command.password());
        BasicMember member = command.toMember(password);
        member.signup(validator);
        return basicMemberRepository.save(member).getId();
    }

    public Long login(String username, String rawPassword) {
        BasicMember member = basicMemberRepository.getByUsername(username);
        member.login(rawPassword, passwordEncoder);
        return member.getId();
    }

    public boolean checkDuplicatedUsername(String username) {
        return basicMemberRepository.existsByUsername(username);
    }
}
