package com.mallang.blog.domain;

import com.mallang.blog.exception.BlogNameException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
@Embeddable
public class BlogName {

    private static final Pattern pattern = Pattern.compile("^(?!-)(?!.*--)[a-z0-9-]{4,32}(?<!-)$");

    private String name;

    protected BlogName() {
    }

    public BlogName(String name) {
        validateDomainName(name);
        this.name = name;
    }

    private void validateDomainName(String name) {
        if (!pattern.matcher(name).matches()) {
            throw new BlogNameException();
        }
    }
}
