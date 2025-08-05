package com.ruoyi.system.domain.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/29
 */
public class PageUtil {

    private PageUtil() {
    }

    public static <T> IPage<T> toMyBatisPlusPage(PageBase pageBase) {
        if (pageBase == null) {
            throw new IllegalArgumentException("pageBase is null");
        } else {
            return new Page(pageBase.getPageNum(), pageBase.getPageSize());
        }
    }

    public static <T> PageData<T> toPubPageData(IPage<T> page) {
        if (page == null) {
            throw new IllegalArgumentException("page is null");
        } else {
            PageData<T> pageData = new PageData();
            pageData.setPageNum(page.getCurrent());
            pageData.setPageSize(page.getSize());
            pageData.setTotal(page.getTotal());
            pageData.setRecords(page.getRecords());
            return pageData;
        }
    }
}
