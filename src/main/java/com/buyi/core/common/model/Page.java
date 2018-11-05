package com.buyi.core.common.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 分页对象
 *
 * @author buyi
 * @date 2017年12月12日下午6:35:17
 * @since 1.0.0
 */
/**
 * @author buyi
 * @since 1.0.0
 * @date 2017下午2:46:59
 * @param <T>
 */
@SuppressWarnings("serial")
public class Page<T> implements Serializable {
	/**
	 * 每页数据量
	 */
	private int size;
	/**
	 * 页码
	 */
	private int pageNum;
	/**
	 * 总记录数
	 */
	private long total;
	/**
	 * 总页数
	 */
	private int totalPage;

	private List<T> content;

	/**
	 * 是否第一页
	 */
	private boolean isFirst = false;
	/**
	 * 是否最后一页
	 */
	private boolean isLast = false;

	public Page() {

	}

	public Page(int pageNum, int totalPage, long total, List<T> content) {
		super();
		this.pageNum = pageNum;
		this.total = total;
		this.totalPage = totalPage;
		this.content = content;
	}


	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public boolean isFirst() {
		// 如果当前页是第一页表示第一页
		return getPageNum() == 1;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isLast() {
		// 如果当前页码是总页数，则表示最后一页
		return getPageNum() == getTotalPage();
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
