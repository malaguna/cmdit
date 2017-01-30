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


import org.malaguna.cmdit.dao.usrmgt.UserDAO;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Participation;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.malaguna.cmdit.service.commands.generic.LoadAbstractObjCmd;
import org.malaguna.cmdit.service.ldap.UserLDAP;
import org.springframework.beans.factory.BeanFactory;

public class LoadUser extends LoadAbstractObjCmd<User, String> {
	private UserLDAP usuarioLdap = null;
	
	public LoadUser(BeanFactory bf) {
		super(bf);
		setDao(getUserDao());
		setAction(ActionHelper.LOAD_USER);
		
		if(bf.containsBean(BeanNames.USER_LDAP))
			usuarioLdap = (UserLDAP)bf.getBean(BeanNames.USER_LDAP);
		
		setReadOnly((usuarioLdap != null)?(!(usuarioLdap.importarUsuarios() || usuarioLdap.refrescarUsuarios())):true);
	}
	
	@Override
	public boolean isValid() {
		return getAction() != null && 
				getDao() != null && 
				getIdObject() != null;
	}

	@Override
	public boolean hasAuthorization(){
		return true;
	}
	
	@Override
	public ResultCommand<User> runCommand() throws Exception {
		User usuario = null;
		String key = null;
		
		usuario = getUserDao().findById(getIdObject());
		
		if(usuarioLdap != null){
			if (usuario == null){
				if (usuarioLdap.importarUsuarios()){
					usuario = usuarioLdap.loadUser(getIdObject());
					String idRol = null;
	
					//Compruebo si debo establecer un rol por defecto
					if (((UserDAO)getDao()).getRolAutoConf() != null){
						String nsrol = usuarioLdap.getUserRol(usuario);
						if(nsrol != null){
							idRol = ((UserDAO)getDao()).getRolAutoConf().get(nsrol);
						}
					}
					
					//Si no se obtuvo nada, preguntar por el rol por defecto
					if (idRol == null || idRol.isEmpty())
						idRol = ((UserDAO)getDao()).getRolDefecto();
	
					//Si el rol indicado existe, se asocia, sino solo se guarda el usuario
					if(idRol != null && !idRol.isEmpty()){
						Participation p = new Participation();
						p.setRol(idRol);
						usuario.getParticipations().add(p);
					}
					
					getDao().persist(usuario);
				}
			}else{
				if (usuarioLdap.refrescarUsuarios()){
					usuario = usuarioLdap.refreshUser(usuario);
					getDao().persist(usuario);
				}
			}
		}
		
		//Comprobaciones de error
		if(usuario == null)
			key = "err.loadUser.notFound";
		else if ( (usuario.getActive() == null) || !usuario.getActive() ) {
			key = "err.loadUser.notActive";
			usuario = null;
		}
		
		//Informar debidamente
		if(key != null){
			createUserComment(key, getIdObject());
			logError(key , null, getIdObject());
		}
			
		this.setResult(usuario);
		return this;
	}	
}
