package com.mallang.category.domain;

import jakarta.annotation.Nullable;

public class AlwaysPassCategoryValidator extends CategoryValidator {

    public static final AlwaysPassCategoryValidator alwaysPassCategoryValidator = new AlwaysPassCategoryValidator();

    private AlwaysPassCategoryValidator() {
        super(null);
    }

    @Override
    public void validateDuplicateNameInSibling(Category category, String name) {
    }

    @Override
    public void validateUpdateHierarchy(
            Category target,
            @Nullable Category parent,
            @Nullable Category prevSibling,
            @Nullable Category nextSibling
    ) {
    }
}
