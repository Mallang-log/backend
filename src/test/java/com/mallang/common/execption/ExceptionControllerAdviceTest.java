package com.mallang.common.execption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

@DisplayName("예외 공통처리 ControllerAdvice (ExceptionControllerAdvice) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ExceptionControllerAdviceTest {

    private final ExceptionControllerAdvice exceptionControllerAdvice = new ExceptionControllerAdvice();

    @Test
    void MallangLogException_와_이를_상속받은_예외를_처리한다() {
        // given
        MallangLogException mallangLogException = new MallangLogException("mallang");
        TestException testException = new TestException("test");

        // when
        ResponseEntity<ErrorCode> ex1 = exceptionControllerAdvice
                .handleException(mallangLogException);
        ResponseEntity<ErrorCode> ex2 = exceptionControllerAdvice
                .handleException(testException);

        // then
        assertThat(ex1.getBody().message()).isEqualTo("mallang");
        assertThat(ex2.getBody().message()).isEqualTo("test");
    }

    @Test
    void MallangLogException_이_처리하지_못하는_예외는_500으로_처리된다() {
        // given
        IllegalAccessException illegalAccessException = new IllegalAccessException();

        // when
        ResponseEntity<ErrorCode> ex1 = exceptionControllerAdvice
                .handleException(illegalAccessException);

        // then
        assertThat(ex1.getBody().status()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    static class TestException extends MallangLogException {
        public TestException(String message) {
            super(message);
        }
    }
}
