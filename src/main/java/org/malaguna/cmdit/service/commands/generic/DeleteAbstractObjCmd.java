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



import org.malaguna.cmdit.dao.DomainObjectDAO;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public abstract class DeleteAbstractObjCmd<T extends AbstractObject<ID>, ID extends Serializable> extends Command {
	private DomainObjectDAO<T, ID> dao = null;
	private String keyMsg = null;
	private T object = null;

	public DeleteAbstractObjCmd(BeanFactory bf) {
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

	public void setKeyMsg(String keyMsg) {
		this.keyMsg = keyMsg;
	}

	public String getKeyMsg() {
		return keyMsg;
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
			   object != null &&
			   dao != null;
	}

	@Override
	public Command runCommand() throws Exception {
		if(getCanLog()){
			if (keyMsg != null)
				createLogComment(keyMsg, object);
			else{
				Object[] args = {this.getClass()};
				String msg = getMessage("err.dab.noMsgKey", args, getLocale());
				throw new Exception(msg);
			}
		}
			
		dao.delete(object);
		return this;
	}
}
