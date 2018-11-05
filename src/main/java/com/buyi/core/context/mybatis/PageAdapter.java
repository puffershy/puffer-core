package com.buyi.core.context.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.buyi.core.common.model.Page;
import com.google.common.collect.Lists;

/**
 * 分页通用对象
 * 
 * @author maojian
 * @date 2017-08-10 09:58:13
 * @since v1.0.0
 */
public class PageAdapter<T> extends ArrayList<T> {

	public static final String PAGE = "_pageNo";

	public static final String SIZE = "_pageSize";

	private static final long serialVersionUID = 1L;

	/** 每页展示条数 */
	private int size = 10;

	/** 当前页码 */
	private int index = 1;

	/** 开始记录偏移量 */
	private int offset = 0;

	/** 总页数 */
	private int totalPage;

	/** 总记录数 */
	private int totalRecord;

	/** mybatis params参数映射 */
	private Map<String, String> qryParams = new HashMap<String, String>(5);

	public PageAdapter() {
	}

	/**
	 * 将PageAdapter转成framework中的Page对象
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:35:36
	 * @since v1.0.0
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Page<T> toPage() {
		List data = Lists.newArrayList(this.toArray());
		return new Page<T>(this.index, this.size, this.totalRecord, data);
	}

	/**
	 * 设置page对象相关的属性
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:35:48
	 * @since v1.0.0
	 */
	public void buildReturnProps() {
		if (totalRecord <= 0) {
			this.totalPage = 0;
			return;
		}
		this.totalPage = (this.totalRecord - 1) / this.size + 1;
	}

	/**
	 * 预处理查询参数
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:36:06
	 * @since v1.0.0
	 * @param index
	 * @param size
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PageAdapter prepareQryParams(int index, int size) {
		if (index <= 0) {
			index = 1;
		}
		this.index = index;
		this.size = size;
		this.offset = (index - 1) * size;// 开始记录偏移量
		return this;
	}

	public Map<String, String> getQryParams() {
		return qryParams;
	}

	public void setQryParams(Map<String, String> qryParams) {
		this.qryParams = qryParams;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public int getSize() {
		return size;
	}

	public int getIndex() {
		return index;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	@Override
	public String toString() {
		return "Page{" + "index=" + index + ",size=" + size + ", totalRecord=" + totalRecord + ", totalPage="
				+ totalPage + ", data=" + JSONObject.toJSONString(this.toArray()) + '}';
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}
