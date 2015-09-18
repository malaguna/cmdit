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
import java.lang.reflect.ParameterizedType;

import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public abstract class RemN1AggregationCmd <T extends AbstractObject<I>, I extends Serializable, S  extends AbstractObject<J>, J extends Serializable> extends Command {
	
	private DomainObjectDAO<T, I> e1Dao = null;
	private DomainObjectDAO<S, J> e2Dao = null;
	private String keyMsg = null;
	private I e1Id = null;
	private J e2Id = null;
	private Class<T> clazzt;


	@SuppressWarnings("unchecked")
	public RemN1AggregationCmd(BeanFactory bf) {
		super(bf);
		setReadOnly(false);
		setCanLog(true);
		clazzt = (Class<T>)	((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];

	}
	
	public String getKeyMsg() {
		return keyMsg;
	}

	public void setKeyMsg(String keyMsg) {
		this.keyMsg = keyMsg;
	}

	protected DomainObjectDAO<T, I> getE1Dao() {
		return e1Dao;
	}

	protected void setE1Dao(DomainObjectDAO<T, I> e1Dao) {
		this.e1Dao = e1Dao;
	}

	protected DomainObjectDAO<S, J> getE2Dao() {
		return e2Dao;
	}

	protected void setE2Dao(DomainObjectDAO<S, J> e2Dao) {
		this.e2Dao = e2Dao;
	}

	public void setE2Id(J object) {
		this.e2Id = object;
	}

	public J getE2Id() {
		return e2Id;
	}

	public I getE1Id() {
		return e1Id;
	}

	public void setE1Id(I e1Id) {
		this.e1Id = e1Id;
	}

	@Override
	public boolean isValid(){
		return  super.isValid() &&
				e2Id != null &&
				e1Dao != null &&
				e1Id != null;
	}

	@Override
	public Command runCommand(){
		T e1Obj = null;
		S e2Obj = null;
		
		e1Obj = e1Dao.findById(e1Id);
		e2Obj = e2Dao.findById(e2Id);

		if(e1Obj != null){
			e1Obj = deleteObject(e1Obj, e2Obj);
			e1Dao.persist(e1Obj);
			if(getCanLog())
				createLogComment(keyMsg, e1Obj.toString(), e2Id.toString());
		}else
			createUserComment("err.ana.noContainer", clazzt.toString(), e1Id);
		
		return this;
	}
	
	protected abstract T deleteObject(T e1Obj, S e2Obj);
}