package com.mallang.auth.domain;

import static java.util.Locale.ENGLISH;

public enum OauthServerType {

    GITHUB,
    ;

    public static OauthServerType fromName(String type) {
        return OauthServerType.valueOf(type.toUpperCase(ENGLISH));
    }
}
