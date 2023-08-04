package com.mallang.auth.domain.oauth;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import com.mallang.auth.exception.UnsupportedOauthTypeException;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthServerType;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OauthMemberClientComposite {

    private final Map<OauthServerType, OauthMemberClient> mapping;

    public OauthMemberClientComposite(Set<OauthMemberClient> clients) {
        mapping = clients.stream()
                .collect(toMap(OauthMemberClient::supportServer, identity()));
    }

    public Member fetch(OauthServerType oauthServerType, String authCode) {
        return getClient(oauthServerType).fetch(authCode);
    }

    private OauthMemberClient getClient(OauthServerType oauthServerType) {
        return Optional.ofNullable(mapping.get(oauthServerType))
                .orElseThrow(UnsupportedOauthTypeException::new);
    }
}
