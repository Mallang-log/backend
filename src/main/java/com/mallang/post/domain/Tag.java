package com.mallang.post.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.ObjectUtils.isEmpty;

import com.mallang.common.domain.CommonDomainModel;
import com.mallang.post.exception.BadTagContentException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = PROTECTED)
public class Tag extends CommonDomainModel {

    @Column(unique = true, nullable = false, length = 30)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public Tag(String content, Post post) {
        validateContent(content);
        this.content = content;
        this.post = post;
    }

    private void validateContent(String content) {
        if (isEmpty(content) || content.length() > 30) {
            throw new BadTagContentException();
        }
    }
}
