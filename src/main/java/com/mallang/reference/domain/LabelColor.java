package com.mallang.reference.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.reference.exception.InvalidLabelColorException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class LabelColor {

    private static final Pattern RGB_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    private String rgbColorCode;

    public LabelColor(String rgbColorCode) {
        validate(rgbColorCode);
        this.rgbColorCode = rgbColorCode;
    }

    private void validate(String rgbColorCode) {
        if (!StringUtils.hasText(rgbColorCode)) {
            throw new InvalidLabelColorException("라벨 색상은 비어있을 수 없습니다.");
        }
        if (!RGB_COLOR_PATTERN.matcher(rgbColorCode).matches()) {
            throw new InvalidLabelColorException("라벨 색상은 #으로 시작해야 하며, 3개의 16진수 (6자리)로 이루어져야 합니다. (예시: #AAAAAA)");
        }
    }
}
