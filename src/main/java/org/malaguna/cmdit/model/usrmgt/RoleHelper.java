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
package org.malaguna.cmdit.model.usrmgt;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleHelper {
	public static final String ADMIN = "admin";
	public static final String BASIC = "basico";
	
	private Map<String, Set<String>> roleMap = null;

	public Map<String, Set<String>> getRoleMap() {
		return roleMap;
	}
	
	public Set<String> getAllRoles() {
		return (roleMap != null)?
			new HashSet<String>(roleMap.keySet()):
			new HashSet<String>();
	}

	public void setRoleMap(Map<String, Set<String>> roleMap) {
		this.roleMap = roleMap;
	}
	
	public Set<String> getActionSet(String role){
		Set<String> result = null;
		
		if(roleMap != null)
			if(role != null)
				result = roleMap.get(role);
		
		return result;
	}
	
	public boolean isAuthorized(String action, String role){
		boolean result = false;
		
		Set<String> actions = getActionSet(role);
		if(actions != null)
			result = actions.contains(action);
		
		return result;
	}
}
