package javax.mycore.common;

import lombok.Data;

import java.util.List;

/**
 * @ClassName Page
 * @Description 分页对象，包含当前页数据及分页信息，如总记录数
 * 能够支持JQuery EasyUI的直接对接，能够支持BootStrap Table直接对接
 * @Author ykq
 * @Date 2021/06/04
 * @Version v1.0.0
 */
@Data
public class Page<T> {
    private static final int DEFAULT_PAGE_SIZE = 20;

    /** 每页的记录数 */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /** 当前页第一条数据在List中的位置,从0开始 */
    private long start;

    /** 当前页中存放的记录,类型一般为List */
    private List<T> rows;

    /** 总记录数 */
    private long total;

    /**
     * 取该页当前页码,页码从1开始.
     */
    public long getPageNo() {
        return start / pageSize + 1;
    }
}
