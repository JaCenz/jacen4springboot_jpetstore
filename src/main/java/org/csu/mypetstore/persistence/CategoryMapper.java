package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Category;

import java.util.List;

public interface CategoryMapper {

    // 得到所有商品大类
    List<Category> getCategoryList();

    // 根据货物ID得到商品大类
    Category getCategory(String categoryId);
}
