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

    private static final int TITLE_MAX_LENGTH = 100;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    public ReferenceLinkTitle(String title) {
        validateNullOrBlank(title);
        this.title = cutting(title);
    }

    private String cutting(String title) {
        title = title.strip();
        if (title.length() > TITLE_MAX_LENGTH) {
            title = title.substring(0, TITLE_MAX_LENGTH - 4) + " ...";
        }
        return title;
    }

    private void validateNullOrBlank(String title) {
        if (!StringUtils.hasText(title)) {
            throw new BadReferenceLinkTitleException("링크의 제목이 입력되지 않았거나 공백으로만 이루어져 있습니다.");
        }
    }
}
