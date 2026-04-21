package io.github.bucheapp.roost.dto.request;

import java.util.List;

public class UserCommunitySearchRequest {
	private int page;
	private int size;
	private List<SortParam> sort;
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public List<SortParam> getSort() {
		return sort;
	}
	public void setSort(List<SortParam> sort) {
		this.sort = sort;
	}
}
