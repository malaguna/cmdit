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
package org.malaguna.cmdit.dao.usrmgt;

import java.util.Map;

import org.malaguna.cmdit.dao.impl.DomainHibernateDAOImpl;
import org.malaguna.cmdit.model.usrmgt.User;

public class UserDAO extends DomainHibernateDAOImpl<User, String> {
	public static String REALM = ""; //Only when using digested passwords
	protected Map<String, String> rolAutoConf = null;
	protected String rolDefecto = null;
	
	public Map<String, String> getRolAutoConf() {
		return rolAutoConf;
	}

	public void setRolAutoConf(Map<String, String> rolAutoConf) {
		this.rolAutoConf = rolAutoConf;
	}

	public String getRolDefecto() {
		return rolDefecto;
	}

	public void setRolDefecto(String rolDefecto) {
		this.rolDefecto = rolDefecto;
	}
}
