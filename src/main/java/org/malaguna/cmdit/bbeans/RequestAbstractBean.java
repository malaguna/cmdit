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
package org.malaguna.cmdit.bbeans;

import javax.el.ELResolver;
import javax.faces.context.FacesContext;

public class RequestAbstractBean extends AbstractBean{
	private SessionAbstractBean session = null;
	private String sessionBeanName = "sessionBean";

	public RequestAbstractBean(){
		super();
	}
	
	protected SessionAbstractBean getSession(){
		if(session == null){
			FacesContext fc = getFacesContext(); 
			ELResolver resolver = fc.getApplication().getELResolver();
			session = (SessionAbstractBean) resolver.getValue(fc.getELContext(), null, sessionBeanName);
		}

		return session;
	}
}
