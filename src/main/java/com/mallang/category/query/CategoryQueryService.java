package com.mallang.category.query;

import com.mallang.blog.domain.BlogName;
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

    public List<CategoryData> findAllByMemberIdAndBlogName(Long memberId, BlogName blogName) {
        return categoryDataDao.findAllByMemberId(memberId, blogName);
    }
}
