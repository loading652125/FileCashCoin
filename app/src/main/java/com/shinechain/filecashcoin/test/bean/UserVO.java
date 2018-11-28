package com.shinechain.filecashcoin.test.bean;

import java.util.List;

/**
 * Created by zjs on 2018/11/22 16:48
 * Describe:
 */
public class UserVO {
	private Long total;
	private List<User> list;
	private Long pageSize;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List<User> getList() {
		return list;
	}

	public void setList(List<User> list) {
		this.list = list;
	}

	public Long getPageSize() {
		return pageSize;
	}

	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "UserVO{" +
				"total=" + total +
				", list=" + list +
				", pageSize=" + pageSize +
				'}';
	}
}
