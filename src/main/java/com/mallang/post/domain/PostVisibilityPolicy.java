package com.mallang.post.domain;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.post.exception.ProtectVisibilityPasswordMustRequired;
import com.mallang.post.exception.VisibilityPasswordNotRequired;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PostVisibilityPolicy {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(nullable = true)
    private String password;

    public PostVisibilityPolicy(Visibility visibility) {
        this(visibility, null);
    }

    public PostVisibilityPolicy(Visibility visibility, String password) {
        validate(visibility, password);
        this.visibility = visibility;
        this.password = password;
    }

    private void validate(Visibility visibility, String password) {
        if (visibility == PUBLIC || visibility == PRIVATE) {
            if (password != null) {
                throw new VisibilityPasswordNotRequired();
            }
        }
        if (visibility == PROTECTED && ObjectUtils.isEmpty(password)) {
            throw new ProtectVisibilityPasswordMustRequired();
        }
    }

    public boolean isVisible(@Nullable String password) {
        return switch (visibility) {
            case PUBLIC -> true;
            case PRIVATE -> false;
            case PROTECTED -> this.password.equals(password);
        };
    }

    public enum Visibility {
        PUBLIC,
        PROTECTED,
        PRIVATE
    }
}
