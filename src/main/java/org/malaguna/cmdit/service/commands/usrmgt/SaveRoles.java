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

import java.util.Iterator;
import java.util.Set;

import org.malaguna.cmdit.model.Participation;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public class SaveRoles extends Command {
	private User usuario = null;
	private Set<String> roles = null;

	public SaveRoles(BeanFactory bf) {
		super(bf);
		setCanLog(true);
		setReadOnly(false);
		setAction(ActionHelper.MANAGE_USERS);
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	@Override 
	public boolean isValid(){
		return super.isValid() &&
				usuario != null &&
				roles != null;
	}
	
	@Override
	public Command runCommand() throws Exception {
		
		usuario = getUserDao().findById(usuario.getPid());
		Iterator<Participation> iPart = usuario.getParticipations().iterator();
		Iterator<String> iRoles = roles.iterator();
		while(iPart.hasNext() && iRoles.hasNext()){
			iPart.next().setRol(iRoles.next());
		}

		getUserDao().persist(usuario);
		
		createLogComment("log.changeRol.ok", usuario.getPid(), roles);
		return this;
	}
}
