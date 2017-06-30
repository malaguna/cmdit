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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Center;
import org.malaguna.cmdit.model.usrmgt.Participation;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.Command;
import org.malaguna.cmdit.service.reflection.ReflectionUtils;
import org.springframework.beans.factory.BeanFactory;

public class SaveRoles extends Command {
	private ReflectionUtils ru = ReflectionUtils.getInstance();
	private User usuario = null;
	private Center center = null;
	private Set<String> roles = null;
	private Set<String> rolesUser = null;

	public SaveRoles(BeanFactory bf) {
		super(bf);
		setCanLog(true);
		setReadOnly(false);
		setAction(ActionHelper.MANAGE_USERS);
	}

	@Override 
	public boolean isValid(){
		return super.isValid() &&
				usuario != null &&
				roles != null;
	}
	
	@Override
	public Command runCommand() throws Exception {
		Set<String> rolesToAdd = new HashSet<String>();
		Set<String> rolesToRemove = new HashSet<String>();
		rolesUser = new HashSet<String>();
		for(Participation p : usuario.getParticipations()){
			rolesUser.add(p.getRol());
		}
		rolesToAdd.addAll(roles);
		rolesToRemove.addAll(rolesUser);
		rolesToAdd.removeAll(rolesUser);
		rolesToRemove.removeAll(roles);
		if(center == null){
			center = getCenterDao().findById(usuario.getDefault_center().getPid());
		}
		Participation p = null;
		for(String r : rolesToAdd){
			p = new Participation();
			p.setRol(r);
			if(center!=null){
				p.setCenter(center);
			}else{
				p.setCenter(usuario.getDefault_center());
			}
			p.setUser(usuario);
			getParticipationDao().persist(p);
		}
		Set<Participation> setAux = new HashSet<Participation>();
		for(Participation part : usuario.getParticipations()){
			if(rolesToRemove.contains(part.getRol())){
				getParticipationDao().delete(part);
				setAux.add(part);
			}
		}
		
		createLogComment("log.changeRol.ok", usuario.getPid(), roles);
		return this;
	}
	
	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
}
