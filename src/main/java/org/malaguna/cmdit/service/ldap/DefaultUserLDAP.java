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
package org.malaguna.cmdit.service.ldap;

import java.util.Date;
import java.util.Locale;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.malaguna.cmdit.model.usrmgt.User;


public class DefaultUserLDAP extends UserLDAP{

	@Override
	public User loadUser(String uid) {
		
		//Recuperamos los atributos del usuario
		Attributes atributos = ldapConn.loadUser(uid);
		
		User usuario = new User();
		
		try{				      
			if (atributos != null) {
	  		  
		    	//Establecemos los atributos deseados
				if (atributos.get("uid") != null){
		    		usuario.setPid((String) atributos.get("uid").get());  
		    	}
		    	  
		    	if (atributos.get("givenName") != null){
		    		usuario.setName((String) atributos.get("givenName").get());  
		    	}
	  	      
		    	if (atributos.get("sn") != null && !"".equals(atributos.get("sn").get())){
		    		usuario.setSurName((String) atributos.get("sn").get());
		    	}
		    	
				//Establecemos la fecha de creación del usuario
		    	usuario.setDate(new Date());
		    	
		    	//Se marca como activo
		    	usuario.setActive(true);
				
			}else{
				usuario = null;
			}		
		} catch (NamingException e) {
			logger.error(messages.getMessage("log.ldap.attribute", new Object[] {e}, Locale.getDefault()));
		}
		
		return usuario;
	}

	@Override
	public User refreshUser(User usuario) {
		//Recuperamos los atributos del usuario
		Attributes atributos = ldapConn.loadUser(usuario.getPid());
		
		try{				      
			if (atributos != null) {
	  		  
		    	//Establecemos los atributos deseados
				if (atributos.get("uid") != null){
		    		usuario.setPid((String) atributos.get("uid").get());  
		    	}
		    	  
		    	if (atributos.get("givenName") != null){
		    		usuario.setName((String) atributos.get("givenName").get());  
		    	}
	  	      
		    	if (atributos.get("sn") != null && !"".equals(atributos.get("sn").get())){
		    		usuario.setSurName((String) atributos.get("sn").get());
		    	}				
			}		
		} catch (NamingException e) {
			logger.error(messages.getMessage("log.ldap.attribute", new Object[] {e}, Locale.getDefault()));
		}
		
		return usuario;	
	}

	@Override
	public String getUserRol(User usuario) {
		String nsrol = null;
		
		//DirContext ctx = ldapConn.getDirContext();
		//SearchControls ctls = new SearchControls();
		//ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		//ctls.setReturningAttributes(new String[] {"nsRole"});
		
		//Recuperamos los atributos del usuario
		//Attributes atributos = ctx.getAttributes("uid=" + usuario.getPid() + "," + context, attrs);
		Attributes atributos = ldapConn.loadUser(usuario.getPid(), new String[] {"nsRole"});
		
		try{				      
			if (atributos != null) {
	  		  
		    	//Obtener rol del usuario en ldap
				if (atributos.get("nsRole") != null){
					nsrol = ((String) atributos.get("nsRole").get());  
		    	}
			}		
		} catch (NamingException e) {
			logger.error(messages.getMessage("log.ldap.attribute", new Object[] {e}, Locale.getDefault()));
		}
		
		return nsrol;
	}
}


