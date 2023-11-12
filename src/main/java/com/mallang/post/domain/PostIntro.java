package com.mallang.post.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.post.exception.InvalidPostIntroLengthException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class PostIntro {

    @Column(nullable = false, length = 250)
    private String intro;

    public PostIntro(String intro) {
        intro = intro.strip();
        validateLength(intro);
        this.intro = intro;
    }

    private void validateLength(String value) {
        int length = value.length();
        if (length < 1 || 250 < length) {
            throw new InvalidPostIntroLengthException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PostIntro postIntro)) {
            return false;
        }
        return Objects.equals(getIntro(), postIntro.getIntro());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIntro());
    }
}
