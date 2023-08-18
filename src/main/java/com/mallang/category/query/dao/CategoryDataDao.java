package com.mallang.category.query.dao;

import com.mallang.category.domain.Category;
import com.mallang.category.query.data.CategoryData;
import com.mallang.category.query.repository.CategoryQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CategoryDataDao {

    private final CategoryQueryRepository categoryQueryRepository;

    public List<CategoryData> findAllByMemberId(Long memberId) {
        List<Category> categories = categoryQueryRepository.findAllRootByMemberId(memberId);
        return categories.stream()
                .map(CategoryData::from)
                .toList();
    }
}
