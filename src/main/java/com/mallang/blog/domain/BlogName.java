package com.mallang.blog.domain;

import com.mallang.blog.exception.BlogNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
@Embeddable
public class BlogName {

    private static final Pattern pattern = Pattern.compile("^(?![-_])(?!.*--)[a-z0-9-_]{4,32}(?<![-_])$");

    @Column(nullable = false, unique = true)
    private String name;

    protected BlogName() {
    }

    public BlogName(String name) {
        name = name.strip();
        validateDomainName(name);
        this.name = name;
    }

    private void validateDomainName(String name) {
        if (!pattern.matcher(name).matches()) {
            throw new BlogNameException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlogName blogName)) {
            return false;
        }
        return Objects.equals(getName(), blogName.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
