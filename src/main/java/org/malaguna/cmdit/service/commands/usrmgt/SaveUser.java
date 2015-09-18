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

import java.util.Date;

import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.generic.SaveAbstractObjCmd;
import org.springframework.beans.factory.BeanFactory;

public class SaveUser extends SaveAbstractObjCmd<User, String> {
	
	public SaveUser(BeanFactory bf) {
		super(bf);
		setDao(getUserDao());
		setAction(ActionHelper.MANAGE_USERS);
	}
	
	@Override
	protected User completeObject(User object) {
		if(object.getDate() == null)
			object.setDate(new Date());
		
		return object;
	}
}