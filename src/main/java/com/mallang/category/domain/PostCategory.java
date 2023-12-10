package com.mallang.category.domain;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.event.PostCategoryDeletedEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
public class PostCategory extends Category<PostCategory> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private PostCategory parent;

    @OneToMany(fetch = LAZY, mappedBy = "parent")
    private List<PostCategory> children = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "previous_sibling_id")
    private PostCategory previousSibling;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "next_sibling_id")
    private PostCategory nextSibling;

    public PostCategory(String name, Member owner, Blog blog) {
        super(name, owner, blog);
    }

    @Override
    public void delete() {
        super.delete();
        registerEvent(new PostCategoryDeletedEvent(getId()));
    }

    @Override
    protected PostCategory self() {
        return this;
    }

    @Override
    protected void setParent(PostCategory category) {
        this.parent = category;
    }

    @Override
    protected void setPreviousSibling(PostCategory previousSibling) {
        this.previousSibling = previousSibling;
    }

    @Override
    protected void setNextSibling(PostCategory nextSibling) {
        this.nextSibling = nextSibling;
    }
}
