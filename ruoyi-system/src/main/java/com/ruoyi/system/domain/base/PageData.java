package com.ruoyi.system.domain.base;

import java.util.List;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/29
 */
public class PageData<T> extends PageBase{
    private List<T> records;
    private Long total;

    public PageData(PageBase pageBase) {
        this.setPageNum(pageBase.getPageNum());
        this.setPageSize(pageBase.getPageSize());
        this.setDoSearchTotal(pageBase.getDoSearchTotal());
    }

    public PageData(PageBase pageBase, List<T> records, Long total) {
        this.setPageNum(pageBase.getPageNum());
        this.setPageSize(pageBase.getPageSize());
        this.setDoSearchTotal(pageBase.getDoSearchTotal());
        this.records = records;
        this.total = total;
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setRecords(final List<T> records) {
        this.records = records;
    }

    public void setTotal(final Long total) {
        this.total = total;
    }

    public String toString() {
        return "PageData(records=" + this.getRecords() + ", total=" + this.getTotal() + ")";
    }

    public PageData() {
    }

    public PageData(final List<T> records, final Long total) {
        this.records = records;
        this.total = total;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageData)) {
            return false;
        } else {
            PageData<?> other = (PageData)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$total = this.getTotal();
                Object other$total = other.getTotal();
                if (this$total == null) {
                    if (other$total != null) {
                        return false;
                    }
                } else if (!this$total.equals(other$total)) {
                    return false;
                }

                Object this$records = this.getRecords();
                Object other$records = other.getRecords();
                if (this$records == null) {
                    if (other$records != null) {
                        return false;
                    }
                } else if (!this$records.equals(other$records)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PageData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $total = this.getTotal();
        result = result * 59 + ($total == null ? 43 : $total.hashCode());
        Object $records = this.getRecords();
        result = result * 59 + ($records == null ? 43 : $records.hashCode());
        return result;
    }
}
