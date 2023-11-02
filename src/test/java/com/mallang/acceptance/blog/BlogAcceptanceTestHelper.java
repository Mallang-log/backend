package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설_요청;

@SuppressWarnings("NonAsciiCharacters")
public class BlogAcceptanceTestHelper {

    public static Long 블로그_개설(
            String 세션_ID,
            String 블로그_이름
    ) {
        return ID를_추출한다(블로그_개설_요청(세션_ID, 블로그_이름));
    }
}
