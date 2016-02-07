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
package org.malaguna.cmdit.bbeans.usrmgt;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.malaguna.cmdit.bbeans.RequestAbstractBean;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.usrmgt.ActiveUser;
import org.malaguna.cmdit.service.commands.usrmgt.FindUser;
import org.malaguna.cmdit.service.commands.usrmgt.LoadUser;
import org.malaguna.cmdit.service.commands.usrmgt.RoleEdition;
import org.malaguna.cmdit.service.commands.usrmgt.SaveRoles;
import org.malaguna.cmdit.service.commands.usrmgt.SaveUser;
import org.primefaces.model.DualListModel;

public class UserMgtMBean extends RequestAbstractBean implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String MANAGE_USERS_NR = "manageUsers"; 

	private User object = null;
	private User fSearch = new User();
	private User fEdition = new User();
	private List<User> objList = null;
	private String password = null;
	private DualListModel<String> roleList = new DualListModel<String>();
	
	public UserMgtMBean(){
		super();
	}

	public User getObject() {
		return object;
	}

	public void setObject(User object) {
		this.object = object;
	}

	public User getfSearch() {
		return fSearch;
	}

	public void setfSearch(User fSearch) {
		this.fSearch = fSearch;
	}

	public User getfEdition() {
		return fEdition;
	}

	public void setfEdition(User fEdition) {
		this.fEdition = fEdition;
	}

	public List<User> getObjList() {
		return objList;
	}

	public void setObjList(List<User> objList) {
		this.objList = objList;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DualListModel<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(DualListModel<String> roleList) {
		this.roleList = roleList;
	}

	public void alPrepareAddition(ActionEvent event){
		fEdition = new User();
	}
		
	public void alPrepareEdition(ActionEvent event){
		User aux = (User) selectRowFromEvent(event.getComponent(), User.class);
		fEdition = (aux != null)?loadObject(aux, ActionHelper.MANAGE_USERS):new User();
	}
		
	public void alPrepareRolEdition(ActionEvent event){
		User aux = (User) selectRowFromEvent(event.getComponent(), User.class);
		fEdition = (aux != null)?loadObject(aux, ActionHelper.MANAGE_USERS):null;
		
		if(fEdition != null){
			RoleEdition cmd = (RoleEdition) createCommand(RoleEdition.class);
			cmd.setUsuario(fEdition);
			cmd = (RoleEdition) runCommand(cmd);
			
			if(cmd != null)
				setRoleList(cmd.getResult());
		}
	}
	
	public void alActiveUser(ActionEvent event){
		User aux = (User) selectRowFromEvent(event.getComponent(), User.class);
		
		ActiveUser cmd = (ActiveUser) createCommand(ActiveUser.class);
		cmd.setObject(aux);
		cmd = (ActiveUser) runCommand(cmd);
		
		if(cmd != null){
			setInfoMessage("Usuario activado", "Se ha reactivado al usuario");
			alFindObject(null);
		}
	}
		
	public void alSaveObject(ActionEvent event){
		SaveUser cmd = null;
		
		cmd = (SaveUser) createCommand(SaveUser.class);
		cmd.setObject(getfEdition());
		runCommand(cmd);
		fEdition = new User();
		
		alFindObject(null);
	}
	
	public void alSaveRoles(ActionEvent event){
		SaveRoles cmd = null;

		if(fEdition != null && roleList != null){
			cmd = (SaveRoles) createCommand(SaveRoles.class);
			cmd.setUsuario(fEdition);
			cmd.setRoles(new HashSet<String>(roleList.getTarget()));
			cmd = (SaveRoles) runCommand(cmd);
			
			if(cmd != null)
				setInfoMessage("Edición de Roles", "El cambio ha sido satisfactorio");
		}
	}
	
	public void alFindObject(ActionEvent event){
		FindUser cmd = null;
		
		cmd = (FindUser) createCommand(FindUser.class);
		cmd.setAction(ActionHelper.MANAGE_USERS);
		cmd.setObject(fSearch);		
		cmd = (FindUser) runCommand(cmd);
		
		if(cmd != null)
			setObjList(cmd.getResult());
		else
			setInfoMessage("Información", "La búsqueda no ha producido ningún resultado");
	}
	
	private User loadObject(User obj, String accion){
		User result = null;
		
		if( (obj != null) && (obj.getPid() != null) ){
			LoadUser cmd = (LoadUser) createCommand(LoadUser.class);
			cmd.setIdObject(obj.getPid());
			cmd = (LoadUser)runCommand(cmd);
			
			if(cmd != null)
				result = cmd.getResult();
		}
		
		return result;
	}	
}
