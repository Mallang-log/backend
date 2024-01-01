package com.mallang.acceptance.common.s3;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.common.infra.s3.presentation.CreatePresignedUrlRequest;
import com.mallang.common.infra.s3.presentation.CreatePresignedUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("S3 Presigned Url 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
public class PresignedUrlAcceptanceTest extends AcceptanceTest {

    @Nested
    class PresignedUrl_생성_API {

        @Test
        void presignedUrl을_생성한다() {
            // when
            CreatePresignedUrlResponse response = given()
                    .body(new CreatePresignedUrlRequest("fileName"))
                    .post("/infra/aws/s3/presigned-url")
                    .then()
                    //.log().all()
                    .extract()
                    .as(CreatePresignedUrlResponse.class);

            // then
            String s = response.presignedUrl();
            assertThat(s).isNotNull();
        }
    }
}
