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

import java.util.Map;
import java.util.Set;

public class ActionHelper {
	public static final String ASOCIATE_ROL_USERS	= "asociarUsuarios";
	public static final String LOAD_USER			= "cargarUsuario";
	public static final String MANAGE_USERS			= "gestionarUsuarios";
	public static final String VIEW_USER_LOGS		= "verLogsUsuario";
	public static final String CHANGE_DIGEST		= "cambiarDigest";
	
	private Map<String, String> actionMap = null;

	public Map<String, String> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, String> actionMap) {
		this.actionMap = actionMap;
	}
	
	public Set<String> getActionSet(){
		Set<String> result = null;
		
		if(actionMap != null)
			result = actionMap.keySet();
		
		return result;
	}
	
	public boolean exists(String action){
		boolean result = false;
		
		if(actionMap != null && action != null)
			result = actionMap.keySet().contains(action);
		
		return result;
	}
}
