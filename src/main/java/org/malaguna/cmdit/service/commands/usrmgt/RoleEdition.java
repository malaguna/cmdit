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
package org.malaguna.cmdit.service.commands.usrmgt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.malaguna.cmdit.model.Participation;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.RoleHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.BeanFactory;

public class RoleEdition extends ResultCommand<DualListModel<String>> {
	private User usuario = null;
	private RoleHelper roleHelper = null;

	public RoleEdition(BeanFactory bf) {
		super(bf);
		setAction(ActionHelper.MANAGE_USERS);
		roleHelper = (RoleHelper) bf.getBean(BeanNames.ROLE_HELPER);
	}

	public void setUsuario(User usuario){
		this.usuario = usuario;
	}
	
	@Override
	public boolean isValid(){
		return super.isValid() &&
				roleHelper != null &&
				usuario != null;
	}
	
	@Override
	public ResultCommand<DualListModel<String>> runCommand() throws Exception {
		DualListModel<String> result = null;
		
		usuario = getUserDao().findById(usuario.getPid());
		
		Set<String> allRoles = roleHelper.getAllRoles();
		Iterator<Participation> iPart = usuario.getParticipations().iterator();
		Set<String> userRoles = new HashSet<String>();
		while(iPart.hasNext()){
			userRoles.add(iPart.next().getRol());
		}
		result = new DualListModel<String>();
		
		allRoles.removeAll(userRoles);
		result.setSource(new ArrayList<String>(allRoles));
		result.setTarget(new ArrayList<String>(userRoles));
		
		this.setResult(result);
		return this;
	}
}
