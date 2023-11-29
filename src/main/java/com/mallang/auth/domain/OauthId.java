package com.mallang.auth.domain;

import static jakarta.persistence.EnumType.STRING;
import static java.util.Locale.ENGLISH;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class OauthId {

    @Column(nullable = false, name = "oauth_server_id")
    private String oauthServerId;

    @Enumerated(STRING)
    @Column(nullable = false, name = "oauth_server")
    private OauthServerType oauthServerType;

    public enum OauthServerType {
        GITHUB,
        ;

        public static OauthServerType fromName(String type) {
            return OauthServerType.valueOf(type.toUpperCase(ENGLISH));
        }
    }
}
