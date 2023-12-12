package com.mallang.category;

import com.mallang.auth.domain.Member;

public interface TieredCategoryValidator {

    void validateNoCategories(Member member);
}
