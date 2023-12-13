package com.mallang.reference.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.common.ServiceTest;
import com.mallang.reference.application.command.CreateLabelCommand;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import com.mallang.reference.application.command.UpdateReferenceLinkCommand;
import com.mallang.reference.domain.ReferenceLink;
import com.mallang.reference.exception.NoAuthorityLabelException;
import com.mallang.reference.exception.NoAuthorityReferenceLinkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("참고 링크 서비스 (ReferenceLinkService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkServiceTest extends ServiceTest {

    private Long memberId;
    private Long otherMemberId;
    private Long myLabelId;
    private Long otherLabelId;
    private SaveReferenceLinkCommand command;


    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        otherMemberId = 회원을_저장한다("동훈");
        myLabelId = labelService.create(new CreateLabelCommand(
                memberId,
                "label",
                "#000000",
                null,
                null
        ));
        otherLabelId = labelService.create(new CreateLabelCommand(
                otherMemberId,
                "label",
                "#000000",
                null,
                null
        ));
        command = new SaveReferenceLinkCommand(
                memberId,
                "https://ttl-blog.tistory.com/",
                "말랑이 블로그",
                "짱 멋있는 말랑이 블로그임",
                myLabelId
        );
    }

    @Nested
    class 참조_링크_저장_시 {

        @Test
        void 참조_링크를_저장한다() {
            // when
            Long linkId = referenceLinkService.save(command);

            // then
            assertThat(linkId).isNotNull();
        }

        @Test
        void 중복되는_url_이어도_괜찮다() {
            // given
            referenceLinkService.save(command);

            // when
            Long id = referenceLinkService.save(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 타인의_라벨을_설정하는_경우_예외() {
            // given
            var command = new SaveReferenceLinkCommand(
                    memberId,
                    "https://ttl-blog.tistory.com/",
                    "말랑이 블로그",
                    "짱 멋있는 말랑이 블로그임",
                    otherLabelId
            );

            // when & then
            assertThatThrownBy(() ->
                    referenceLinkService.save(command)
            ).isInstanceOf(NoAuthorityLabelException.class);
        }
    }

    @Nested
    class 참조_링크_업데이트_시 {

        @Test
        void 주인이_아니라면_예외() {
            // given
            Long id = referenceLinkService.save(command);
            Long otherMemberId = 회원을_저장한다("other");
            UpdateReferenceLinkCommand updateCommand = new UpdateReferenceLinkCommand(
                    id,
                    otherMemberId,
                    "수정 url",
                    "수정 제목",
                    "수정 메모",
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                referenceLinkService.update(updateCommand);
            }).isInstanceOf(NoAuthorityReferenceLinkException.class);
        }

        @Test
        void 정보를_업데이트한다() {
            // given
            Long id = referenceLinkService.save(command);
            UpdateReferenceLinkCommand updateCommand = new UpdateReferenceLinkCommand(
                    id,
                    memberId,
                    "수정 url",
                    "수정 제목",
                    "수정 메모",
                    null
            );

            // when
            referenceLinkService.update(updateCommand);

            // then
            ReferenceLink link = referenceLinkRepository.getById(id);
            assertThat(link.getUrl()).isEqualTo("수정 url");
        }

        @Test
        void 타인의_라벨로_업데이트하는경우_예외() {
            // given
            Long id = referenceLinkService.save(command);
            UpdateReferenceLinkCommand updateCommand = new UpdateReferenceLinkCommand(
                    id,
                    memberId,
                    "수정 url",
                    "수정 제목",
                    "수정 메모",
                    otherLabelId
            );

            // when & then
            assertThatThrownBy(() -> {
                referenceLinkService.update(updateCommand);
            }).isInstanceOf(NoAuthorityLabelException.class);
        }
    }

    @Nested
    class 참조_링크_삭제_시 {

        @Test
        void 링크를_삭제한다() {
            // given
            Long id = referenceLinkService.save(command);

            // when
            referenceLinkService.delete(id, memberId);

            // then
            assertThat(referenceLinkRepository.existsById(id)).isFalse();
        }

        @Test
        void 주인이_아니라면_예외() {
            // given
            Long id = referenceLinkService.save(command);
            Long otherMemberId = 회원을_저장한다("other");

            // when & then
            assertThatThrownBy(() -> {
                referenceLinkService.delete(id, otherMemberId);
            }).isInstanceOf(NoAuthorityReferenceLinkException.class);
        }
    }
}
