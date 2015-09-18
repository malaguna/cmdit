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

import org.apache.log4j.Logger;
import org.malaguna.cmdit.model.usrmgt.User;
import org.springframework.context.MessageSource;

public abstract class UserLDAP {
	protected LDAPBase ldapConn;
    protected MessageSource messages = null;
    protected Logger logger = Logger.getLogger(this.getClass());
	
	public abstract User loadUser(String uid);
	public abstract User refreshUser(User user);
	public abstract String getUserRol(User user);
	
	public LDAPBase getLdapConn() {
		return ldapConn;
	}

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	public void setLdapConn(LDAPBase ldapConn) {
		this.ldapConn = ldapConn;
	}
	
	public boolean importarUsuarios(){
		return ldapConn.isImportUser();
	}
	
	public boolean refrescarUsuarios(){
		return ldapConn.isRefreshUser();
	}
}


