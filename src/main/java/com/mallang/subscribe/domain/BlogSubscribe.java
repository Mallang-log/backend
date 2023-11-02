package com.mallang.subscribe.domain;

import com.mallang.blog.domain.Blog;
import com.mallang.common.domain.CommonDomainModel;
import com.mallang.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BlogSubscribe extends CommonDomainModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id")
    private Member subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public BlogSubscribe(Member subscriber, Blog blog) {
        this.subscriber = subscriber;
        this.blog = blog;
    }

    public void subscribe(BlogSubscribeValidator validator) {
        validator.validateSubscribe(this);
    }
}
