package com.wanbowen.mall.dao;

import com.wanbowen.mall.dto.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * 商品分类自定义Dao
 * Created by macro on 2018/5/25.
 */
public interface PmsProductCategoryDao {
    /**
     * 获取商品分类包括子分类
     */
    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
