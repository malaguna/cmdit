/**
 * This file is part of CMDit.
 *
 * CMDit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CMDit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CMDit.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.malaguna.cmdit.service.commands.model;

import java.util.List;
import java.util.Map;

public class QueryDataLazy<T> {
	
	public static final String ORDER_ASC = "ASC";
	public static final String ORDER_DES = "DESC";

	private List<T> result;
	private Long totalResultCount;
	
	private int start;
	private int end;
	private String sortField;
	private String order;
	private Map<String,Object> filters;
	
	public QueryDataLazy(int start, int end, String field, String order, Map<String, Object> filters) {
	
		this.start = start;
		this.end = end;
		this.sortField = field;
		this.order = order;
		this.filters = filters;
	}
	
	@Override
	public String toString() {
		return "QueryDataLazy [start=" + start + ", end=" + end + ", sortField=" + sortField + ", order=" + order + ", filters="
				+ filters + "]";
	}


	public Long getTotalResultCount() {
		return totalResultCount;
	}


	public void setTotalResultCount(Long totalResultCount) {
		this.totalResultCount = totalResultCount;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	public String getSortField() {
		return sortField;
	}


	public void setSortField(String sortField) {
		this.sortField = sortField;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	public Map<String, Object> getFilters() {
		return filters;
	}


	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}


	public List<T> getResult() {
		return result;
	}


	public void setResult(List<T> result) {
		this.result = result;
	}	
}
