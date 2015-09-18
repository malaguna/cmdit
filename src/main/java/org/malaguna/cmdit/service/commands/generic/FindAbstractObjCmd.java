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
package org.malaguna.cmdit.service.commands.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.dao.RelatedCriteria;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public abstract class FindAbstractObjCmd<T extends AbstractObject<ID>, ID extends Serializable> extends ResultCommand<List<T>> {
	private DomainObjectDAO<T, ID> dao = null;
	private String[] exclude = null;
	private Integer limit = null;
	private T object = null;

	public FindAbstractObjCmd(BeanFactory bf) {
		super(bf);
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String[] getExclude() {
		return exclude;
	}

	public void setExclude(String[] exclude) {
		this.exclude = exclude;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getLimit() {
		return limit;
	}

	protected DomainObjectDAO<T, ID> getDao() {
		return dao;
	}

	protected void setDao(DomainObjectDAO<T, ID> dao) {
		this.dao = dao;
	}

	@Override
	public boolean isValid(){
		return super.isValid() &&
			   dao != null;
	}
	
	private String[] completeExclude(){
		String[] include = {"oid", "objectVersion"};
		String[] result = exclude;
		
		if(exclude == null)
			result = include;
		else
			for(String prop : include){
				boolean isIncluded = false;
				int i = 0;
				
				while ( (i < exclude.length) && (!isIncluded) )
					isIncluded = prop.equalsIgnoreCase(exclude[i++]);
				
				if(!isIncluded){
					result = new String[exclude.length + 1];
					
					for(int j = 0; j < exclude.length; j++)
						result[j] = exclude[j];
					
					result[exclude.length] = prop;
					exclude = result;
				}
			}
		
		return result;
	}

	@Override
	public ResultCommand<List<T>> runCommand() throws Exception {
		RelatedCriteria[] auxRelations = completeRelations();
		String[] auxExclude = completeExclude();
		Long resultCount = null;
		
		if( (object != null) && (object.getPid() != null) && (!object.getPid().toString().equalsIgnoreCase("")) ){
			T aux = dao.findById(object.getPid());
			if(aux != null){
				this.setResult(new ArrayList<T>());
				this.getResult().add(aux);
			}			
		}else{		
			if(limit != null)
				resultCount = dao.findByCriteriaCount(object, auxExclude, auxRelations);
				
			if( (resultCount == null) || (resultCount <= limit) )
				this.setResult(dao.findByCriteria(object, auxExclude, auxRelations));
			else
				createUserComment("warn.faoc.limited", resultCount, limit);
		}
		
		return this;
	}
	
	protected abstract RelatedCriteria[] completeRelations();
}