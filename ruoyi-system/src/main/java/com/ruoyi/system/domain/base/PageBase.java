package com.ruoyi.system.domain.base;

import lombok.Data;

import java.io.Serializable;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/29
 */
@Data
public class PageBase implements Serializable {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private Boolean doSearchTotal = true;

    public Long getPageNum() {
        return this.pageNum;
    }

    public Long getPageSize() {
        return this.pageSize;
    }

    public Boolean getDoSearchTotal() {
        return this.doSearchTotal;
    }

    public PageBase setPageNum(final Long pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    public PageBase setPageSize(final Long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public PageBase setDoSearchTotal(final Boolean doSearchTotal) {
        this.doSearchTotal = doSearchTotal;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageBase)) {
            return false;
        } else {
            PageBase other = (PageBase)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$currentPage = this.getPageNum();
                Object other$currentPage = other.getPageNum();
                if (this$currentPage == null) {
                    if (other$currentPage != null) {
                        return false;
                    }
                } else if (!this$currentPage.equals(other$currentPage)) {
                    return false;
                }

                Object this$pageSize = this.getPageSize();
                Object other$pageSize = other.getPageSize();
                if (this$pageSize == null) {
                    if (other$pageSize != null) {
                        return false;
                    }
                } else if (!this$pageSize.equals(other$pageSize)) {
                    return false;
                }

                Object this$doSearchTotal = this.getDoSearchTotal();
                Object other$doSearchTotal = other.getDoSearchTotal();
                if (this$doSearchTotal == null) {
                    if (other$doSearchTotal != null) {
                        return false;
                    }
                } else if (!this$doSearchTotal.equals(other$doSearchTotal)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PageBase;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $currentPage = this.getPageNum();
        result = result * 59 + ($currentPage == null ? 43 : $currentPage.hashCode());
        Object $pageSize = this.getPageSize();
        result = result * 59 + ($pageSize == null ? 43 : $pageSize.hashCode());
        Object $doSearchTotal = this.getDoSearchTotal();
        result = result * 59 + ($doSearchTotal == null ? 43 : $doSearchTotal.hashCode());
        return result;
    }

    public String toString() {
        return "PageBase(currentPage=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ", doSearchTotal=" + this.getDoSearchTotal() + ")";
    }

    public PageBase() {
    }

    public PageBase(final Long currentPage, final Long pageSize, final Boolean doSearchTotal) {
        this.pageNum = currentPage;
        this.pageSize = pageSize;
        this.doSearchTotal = doSearchTotal;
    }
}
