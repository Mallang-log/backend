package com.mallang.blog.domain;

import com.mallang.blog.exception.BlogDomainNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public class BlogDomain {

    private static final Pattern pattern = Pattern.compile("^(?!-)(?!.*--)[a-z0-9-]{4,32}(?<!-)$");

    @Column(unique = true, updatable = false)
    private String domainName;

    protected BlogDomain() {
    }

    public BlogDomain(String domainName) {
        validateDomainName(domainName);
        this.domainName = domainName;
    }

    private void validateDomainName(String name) {
        if (!pattern.matcher(name).matches()) {
            throw new BlogDomainNameException();
        }
    }
}
