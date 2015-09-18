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

import org.hibernate.NonUniqueObjectException;
import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public abstract class SaveAbstractObjCmd<T extends AbstractObject<ID>, ID extends Serializable> extends Command {
	private DomainObjectDAO<T, ID> dao = null;
	private String updateKeyMsg = "log.modifyGeneric.ok";
	private String createKeyMsg = "log.createGeneric.ok";
	private T object = null;
	private boolean isUpdate = false;
	private boolean autoMerge = false;

	public SaveAbstractObjCmd(BeanFactory bf) {
		super(bf);
		setReadOnly(false);
		setCanLog(true);
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String getUpdateKeyMsg() {
		return updateKeyMsg;
	}

	public void setUpdateKeyMsg(String updateKeyMsg) {
		this.updateKeyMsg = updateKeyMsg;
	}

	public String getCreateKeyMsg() {
		return createKeyMsg;
	}

	public void setCreateKeyMsg(String saveKeyMsg) {
		this.createKeyMsg = saveKeyMsg;
	}

	protected DomainObjectDAO<T, ID> getDao() {
		return dao;
	}

	protected void setDao(DomainObjectDAO<T, ID> dao) {
		this.dao = dao;
	}

	public boolean isAutoMerge() {
		return autoMerge;
	}

	public void setAutoMerge(boolean autoMerge) {
		this.autoMerge = autoMerge;
	}

	@Override
	public boolean isValid(){
		return super.isValid() &&
			   object != null &&
			   dao != null;
	}
	
	protected void createLogComment() {
		if(getCanLog()){
			if ( (updateKeyMsg != null) && (createKeyMsg != null) ){
				super.createLogComment((isUpdate?updateKeyMsg:createKeyMsg), object, object.getClass().getSimpleName());
			}else{
				Object[] args = {this.getClass()};
				logError("err.dab.noMsgKey", null, args);
			}
		}
	}
	
    @Override
    public void endLog(){
    	//Crear log de salvado correctamente
    	createLogComment();         
    	super.endLog();
    }

	@Override
	public Command runCommand() throws Exception {
		isUpdate = (object.getPid() != null);
		T aux = completeObject(object);
		
		try{
			object = (aux != null)?aux:object;
			dao.persist(object);
		}catch(NonUniqueObjectException nuoe){
			if(autoMerge){
				dao.merge(object);
			}else
				throw nuoe;
		}
		
		return this;
	}
	
	protected abstract T completeObject(T object);
}
