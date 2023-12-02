package com.mallang.reference.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.reference.exception.BadReferenceLinkUrlException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class ReferenceLinkUrl {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    public ReferenceLinkUrl(String url) {
        validate(url);
        this.url = url.strip();
    }

    private void validate(String url) {
        if (!StringUtils.hasText(url)) {
            throw new BadReferenceLinkUrlException();
        }
    }
}
