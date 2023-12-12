package com.mallang.post.domain.star;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.auth.domain.Member;
import com.mallang.category.TieredCategory;
import com.mallang.post.exception.NoAuthorityStarGroupException;
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
public class StarGroup extends TieredCategory<StarGroup> {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private StarGroup parent;

    @OneToMany(fetch = LAZY, mappedBy = "parent")
    private List<StarGroup> children = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "prev_sibling_id")
    private StarGroup previousSibling;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "next_sibling_id")
    private StarGroup nextSibling;

    public StarGroup(String name, Member owner) {
        super(name, owner);
    }

    @Override
    public void validateOwner(Member member) {
        if (!owner.equals(member)) {
            throw new NoAuthorityStarGroupException();
        }
    }

    @Override
    protected StarGroup self() {
        return this;
    }

    @Override
    protected void setParent(StarGroup category) {
        this.parent = category;
    }

    @Override
    protected void setPreviousSibling(StarGroup previousSibling) {
        this.previousSibling = previousSibling;
    }

    @Override
    protected void setNextSibling(StarGroup nextSibling) {
        this.nextSibling = nextSibling;
    }
}
