package com.mallang.auth.query;

import com.mallang.auth.query.repository.BasicMemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BasicAuthQueryService {

    private final BasicMemberQueryRepository basicMemberQueryRepository;

    public boolean checkDuplicatedUsername(String username) {
        return basicMemberQueryRepository.existsByUsername(username);
    }
}
