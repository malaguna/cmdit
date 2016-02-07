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
