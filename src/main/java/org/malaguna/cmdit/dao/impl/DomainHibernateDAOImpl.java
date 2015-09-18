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
package org.malaguna.cmdit.dao.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.dao.RelatedCriteria;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.model.QueryDataLazy;

/**
 * This class ensures entity type extends AbstractObject
 * 
 * @author miguel
 *
 */
public abstract class DomainHibernateDAOImpl<T extends AbstractObject<ID>, ID extends Serializable> extends BasicHibernateDAOImpl<T, ID> implements DomainObjectDAO<T, ID>{
		
	@Override
	public List<T> findAll(){
		List<T> result = null;
		
		result = super.findAll();
		Collections.sort(result);
		
		return result;
	}
	
	@Override
	public List<T> findByCriteria(T sample, String[] excludeProps, RelatedCriteria[] relations){
		List<T> result = null;
		
		result = super.findByCriteria(sample, excludeProps, relations);
		Collections.sort(result);
		
		return result;
	}

	protected Criteria listLazyResult(Criteria criteria, QueryDataLazy<?> query){
		criteria = prepareLazyCriteria(criteria, query);
		criteria.setMaxResults(query.getEnd());
		criteria.setFirstResult(query.getStart());
				
		return criteria;
	}
	
	protected Long totalLazyResultCount(Criteria criteria, QueryDataLazy<?> query){
		criteria = prepareLazyCriteria(criteria, query);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void addSortOrder(Criteria criteria, String sortField, String order){
		if(sortField.contains(".")){
			StringTokenizer st = new StringTokenizer(sortField, ".");
			int total = st.countTokens();
		
			Criteria aux = criteria;
			for(int i = 1; i < total; i++){
				aux = aux.createCriteria(st.nextToken());
			}
		
			criteria = aux;
			sortField = st.nextToken();
		}
		
		if(order.equals(QueryDataLazy.ORDER_ASC))
			criteria.addOrder(Order.asc(sortField));
		if(order.equals(QueryDataLazy.ORDER_DES))
			criteria.addOrder(Order.desc(sortField));
	}
	
	private Criteria prepareLazyCriteria(Criteria criteria, QueryDataLazy<?> query){
		
		if (query.getSortField() != null && !query.getSortField().isEmpty()){
			if (query.getOrder() != null) {
				addSortOrder(criteria, query.getSortField(), query.getOrder());
			}
		}
		
		if (!query.getFilters().isEmpty()) {
			
			Iterator<Entry<String, Object>> iterator = query.getFilters().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				String value = null;
				
				if(entry.getValue() instanceof String)
					value = (String) entry.getValue();
				else
					value = entry.getValue().toString();
				
				if(value != null){
					criteria.add(Restrictions.ilike(entry.getKey(), value.replace('*', '%'), MatchMode.ANYWHERE));
				}
			}
		}

		return criteria;
	}
}