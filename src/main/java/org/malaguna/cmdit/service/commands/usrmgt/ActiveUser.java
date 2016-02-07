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

import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

public class ActiveUser extends Command {
	private User object = null;

	public ActiveUser(BeanFactory bf) {
		super(bf);
		setCanLog(true);
		setReadOnly(false);
		setAction(ActionHelper.MANAGE_USERS);
	}
	
	@Override
	public boolean isValid(){
		return super.isValid() && 
			object != null && object.getPid() != null;
	}

	@Override
	public Command runCommand() throws Exception {
		object = getUserDao().findById(object.getPid());
		object.setActive(Boolean.TRUE);
		getUserDao().persist(object);
		
		createLogComment("user.active.log", object);
		
		return this;
	}

	public User getObject() {
		return object;
	}

	public void setObject(User user) {
		this.object = user;
	}
}
