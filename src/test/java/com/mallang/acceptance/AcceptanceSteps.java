package com.mallang.acceptance;

import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public final class AcceptanceSteps {

    public static final String JSESSION_ID = "JSESSIONID";
    public static final HttpStatus 정상_처리 = HttpStatus.OK;
    public static final HttpStatus 생성됨 = HttpStatus.CREATED;
    public static final HttpStatus 잘못된_요청 = HttpStatus.BAD_REQUEST;
    public static final HttpStatus 권한_없음 = HttpStatus.FORBIDDEN;
    public static final HttpStatus 찾을수_없음 = HttpStatus.NOT_FOUND;
    public static final HttpStatus 중복됨 = HttpStatus.CONFLICT;

    public static <T> T 없음() {
        return null;
    }

    public static <T> List<T> 비어있음() {
        return Collections.emptyList();
    }

    public static RequestSpecification given() {
        return RestAssured
                .given().log().all()
                .contentType(JSON);
    }

    public static RequestSpecification given(String 세션_ID) {
        return RestAssured
                .given().log().all()
                .cookie(JSESSION_ID, 세션_ID)
                .contentType(JSON);
    }

    public static void 응답_상태를_검증한다(
            ExtractableResponse<Response> 응답,
            HttpStatus 예상_상태
    ) {
        assertThat(응답.statusCode()).isEqualTo(예상_상태.value());
    }

    public static Long ID를_추출한다(
            ExtractableResponse<Response> 응답
    ) {
        String location = 응답.header("Location");
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    public static void 값이_존재한다(Object o) {
        assertThat(o).isNotNull();
    }
}
