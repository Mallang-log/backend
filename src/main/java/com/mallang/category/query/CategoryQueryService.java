package com.mallang.category.query;

import com.mallang.category.query.dao.CategoryDataDao;
import com.mallang.category.query.data.CategoryData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryQueryService {

    private final CategoryDataDao categoryDataDao;

    public List<CategoryData> findAllByMemberId(Long memberId) {
        return categoryDataDao.findAllByMemberId(memberId);
    }
}
