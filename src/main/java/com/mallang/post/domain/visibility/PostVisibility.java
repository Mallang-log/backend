package com.mallang.post.domain.visibility;

import com.mallang.post.exception.ProtectVisibilityPasswordMustRequired;
import com.mallang.post.exception.VisibilityPasswordNotRequired;
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
public class PostVisibility {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(nullable = true)
    private String password;

    public PostVisibility(Visibility visibility) {
        this(visibility, null);
    }

    public PostVisibility(Visibility visibility, String password) {
        validate(visibility, password);
        this.visibility = visibility;
        this.password = password;
    }

    private void validate(Visibility visibility, String password) {
        if (visibility == Visibility.PUBLIC || visibility == Visibility.PRIVATE) {
            if (password != null) {
                throw new VisibilityPasswordNotRequired();
            }
        }
        if (visibility == Visibility.PROTECTED) {
            if (ObjectUtils.isEmpty(password)) {
                throw new ProtectVisibilityPasswordMustRequired();
            }
        }
    }

    public enum Visibility {

        PUBLIC,
        PROTECTED,
        PRIVATE
    }
}
