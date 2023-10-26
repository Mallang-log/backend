package com.mallang.category.query.dao;

import com.mallang.category.query.dao.support.CategoryQuerySupport;
import com.mallang.category.query.data.CategoryData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CategoryDataDao {

    private final CategoryQuerySupport categoryQuerySupport;

    public List<CategoryData> findAllByMemberId(Long memberId) {
        return categoryQuerySupport.findAllRootByMemberId(memberId)
                .stream()
                .map(CategoryData::from)
                .toList();
    }
}
