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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public abstract class ResyncPersistentObject<T extends AbstractObject<ID>, ID extends Serializable> extends ResultCommand<T> {
	private T object = null;
	private SessionFactory sf = null;
	
	public ResyncPersistentObject(BeanFactory bf) {
		super(bf);
		sf = getUserDao().getSessionFactory();
	}

	public void setObject(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	/* Permite escoger una Factoría de sesiones diferente a la de Jonic */
	public void setSfBeanName(String sfBeanName) {
		if(sfBeanName != null){
			SessionFactory aux = (SessionFactory)getBeanFactory().getBean(sfBeanName);
			if(aux != null)
				sf = aux;
		}
	}

	@Override
	public boolean hasAuthorization(){
		return true;
	}
	
	@Override
	public boolean isValid(){
		return object != null &&
			object.getPid() != null &&
			sf != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResultCommand<T> runCommand() throws Exception {
		Session session = sf.getCurrentSession();
		
		object = (T) session.load(object.getClass(), object.getPid());
		
		this.setResult(object);
		return this;
	}
}
