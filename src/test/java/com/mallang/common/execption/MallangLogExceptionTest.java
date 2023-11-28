package com.mallang.common.execption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("대표 예외 (MallangLogException) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MallangLogExceptionTest {

    @Test
    void ErrorCode를_받아_생성되며_이떄_예외_메세지는_ErrorCode의_메세지이다() {
        // when
        MallangLogException mallangLogException = new MallangLogException(
                new ErrorCode(BAD_REQUEST, "test")
        );

        // then
        assertThat(mallangLogException.getMessage()).isEqualTo("test");
        assertThat(mallangLogException.getErrorCode().message()).isEqualTo("test");
        assertThat(mallangLogException.getErrorCode().status()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void ErrorCode_없이_생성되면_기본적으로_500_예외이다() {
        // given
        MallangLogException ex1 = new MallangLogException();
        MallangLogException ex2 = new MallangLogException("test");

        // when & then
        assertThat(ex1.getErrorCode().status())
                .isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(ex2.getErrorCode().status())
                .isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(ex2.getErrorCode().message()).isEqualTo("test");
    }
}
