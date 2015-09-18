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

public abstract class AddN1AggregationCmd <T extends AbstractObject<I>, I extends Serializable, S extends AbstractObject<?>> extends Command {
	
	private DomainObjectDAO<T, I> e1Dao = null;
	private String keyMsg = null;
	private S object = null;
	private I e1Id = null;
	private Class<T> clazzt;


	@SuppressWarnings("unchecked")
	public AddN1AggregationCmd(BeanFactory bf) {
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

	public void setObject(S object) {
		this.object = object;
	}

	public S getObject() {
		return object;
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
				object != null &&
				e1Dao != null &&
				e1Id != null;
	}

	@Override
	public Command runCommand(){
		T e1Obj = null;
		
		e1Obj = e1Dao.findById(e1Id);

		if(e1Obj != null){
			e1Obj = relateAndCompleteObject(e1Obj, object);
			e1Dao.persist(e1Obj);
			createLogComment(keyMsg, e1Obj.toString(), object.toString());
		}else
			createUserComment("err.ana.noContainer", clazzt.toString(), e1Id);
		
		return this;
	}
	
	protected abstract T relateAndCompleteObject(T e1Obj, S e2Obj);
}