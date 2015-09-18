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
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public abstract class LoadAbstractObjCmd<T extends AbstractObject<ID>, ID extends Serializable> extends ResultCommand<T> {
	private ID idObject = null;
	private DomainObjectDAO<T, ID> dao = null;

	public LoadAbstractObjCmd(BeanFactory bf) {
		super(bf);
	}
	
	public ID getIdObject() {
		return idObject;
	}

	public void setIdObject(ID idObject) {
		this.idObject = idObject;
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
			   idObject != null &&
			   dao != null;
	}

	@Override
	public ResultCommand<T> runCommand() throws Exception {
		this.setResult(dao.findById(idObject));
		return this;
	}
}
