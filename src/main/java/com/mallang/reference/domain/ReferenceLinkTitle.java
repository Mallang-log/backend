package com.mallang.reference.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.reference.exception.BadReferenceLinkTitleException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class ReferenceLinkTitle {

    private static final int TITLE_MAX_LENGTH = 30;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    public ReferenceLinkTitle(String title) {
        validate(title);
        this.title = title.strip();
    }

    private void validate(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BadReferenceLinkTitleException("링크의 제목이 입력되지 않았거나 공백으로만 이루어져 있습니다.");
        }
        title = title.strip();
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new BadReferenceLinkTitleException("링크 제목의 길이는 최대 " + TITLE_MAX_LENGTH + "글자입니다.");
        }
    }
}
