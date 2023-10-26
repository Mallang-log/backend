package com.mallang.category.domain;

public class AlwaysPassCategoryValidator extends CategoryValidator {

    public static final AlwaysPassCategoryValidator alwaysPassCategoryValidator = new AlwaysPassCategoryValidator();

    private AlwaysPassCategoryValidator() {
        super(null);
    }

    @Override
    public void validateDuplicateRootName(Long memberId, String name) {
    }
}
