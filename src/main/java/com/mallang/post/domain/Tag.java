package com.mallang.post.domain;

import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.exception.BadTagContentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Tag extends CommonDomainModel {

    @Column(nullable = false, length = 30)
    private String content;

    public Tag(String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(String content) {
        if (isEmpty(content) || content.length() > 30) {
            throw new BadTagContentException();
        }
    }
}
