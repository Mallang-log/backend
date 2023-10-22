package com.mallang.acceptance.auth;

import static com.mallang.acceptance.AcceptanceSteps.JSESSION_ID;
import static com.mallang.acceptance.AcceptanceSteps.given;

@SuppressWarnings("NonAsciiCharacters")
public class AuthAcceptanceSteps {

    public static String 회원가입과_로그인_후_세션_ID_반환(String 닉네임) {
        return given()
                .queryParam("code", 닉네임)
                .post("/oauth/login/github")
                .then()
                .extract()
                .cookie(JSESSION_ID);
    }
}
