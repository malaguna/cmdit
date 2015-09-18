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
import java.util.List;

import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public abstract class AddNMRelationListCmd<T extends AbstractObject<I>, I extends Serializable, S extends AbstractObject<J>, J extends Serializable> extends Command {
	private DomainObjectDAO<T, I> e1Dao = null;
	private DomainObjectDAO<S, J> e2Dao = null;
	private String keyMsg = null;
	private List<J> e2Ids = null;
	private I e1Id = null;

	public AddNMRelationListCmd(BeanFactory bf) {
		super(bf);
		setReadOnly(false);
		setCanLog(true);
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

	public I getE1Id() {
		return e1Id;
	}

	public void setE1Id(I e1Id) {
		this.e1Id = e1Id;
	}

	public  List<J> getE2Ids() {
		return e2Ids;
	}

	public void setE2Ids( List<J> e2Ids) {
		this.e2Ids = e2Ids;
	}

	@Override
	public boolean isValid(){
		return  super.isValid() &&
				e1Dao != null &&
				e2Dao != null &&
				e1Id != null &&
				e2Ids != null;
	}

	@Override
	public Command runCommand(){
		String logCommment = "";
		T e1Obj = null;
		S e2Obj = null;
		
		e1Obj = e1Dao.findById(e1Id);
		
		for(J aux : e2Ids){
			e2Obj = e2Dao.findById(aux);
			if(e2Obj != null){
				e1Obj = relateAndcompleteObjects(e1Obj, e2Obj);
				logCommment += e2Obj.toString() + ", ";
			}
		}

		e1Dao.persist(e1Obj);
		createLogComment(keyMsg, e1Obj.toString(), logCommment);
		return this;
	}
	
	protected abstract T relateAndcompleteObjects(T e1Obj, S e2Obj);
}