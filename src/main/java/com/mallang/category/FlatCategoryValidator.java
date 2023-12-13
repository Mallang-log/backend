package com.mallang.category;

import com.mallang.auth.domain.Member;

public interface FlatCategoryValidator {

    void validateNoCategories(Member member);

    void validateDuplicateName(Member member, String name);
}
