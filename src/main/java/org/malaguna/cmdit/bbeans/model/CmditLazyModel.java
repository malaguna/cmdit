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
package org.malaguna.cmdit.bbeans.model;

import java.util.List;
import java.util.Map;

import org.malaguna.cmdit.bbeans.LazyLoadCallBack;
import org.malaguna.cmdit.service.commands.model.QueryDataLazy;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class CmditLazyModel<T> extends LazyDataModel<T> {
	private static final long serialVersionUID = 1L;
	private LazyLoadCallBack<T> runner = null;
	
	public CmditLazyModel(LazyLoadCallBack<T> runner){
		this.runner = runner;
	}
	
	@Override
	public List<T> load(int first, int pageSize,
			String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		QueryDataLazy<T> result = null;
		
		int start = first;
		int end = first + pageSize;

		String order = QueryDataLazy.ORDER_ASC;
		if (sortOrder == SortOrder.DESCENDING) {
			order = QueryDataLazy.ORDER_DES;
		}

		QueryDataLazy<T> qData = new QueryDataLazy<T>(start, end,
				sortField, order, filters);

		if(runner != null)
			result = runner.invokeLazyLoadCommand(qData);

		int count = qData.getTotalResultCount().intValue();
		this.setRowCount(count);
		return result.getResult();
	}
}
