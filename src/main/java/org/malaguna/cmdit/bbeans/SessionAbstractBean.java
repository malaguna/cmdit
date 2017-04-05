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
package org.malaguna.cmdit.bbeans;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.malaguna.cmdit.model.usrmgt.Center;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.CommandException;
import org.malaguna.cmdit.service.commands.usrmgt.LoadUser;
import org.primefaces.model.menu.MenuModel;

public class SessionAbstractBean extends AbstractBean {
	private MenuModel menu = null;
	private Center center = null;
	private User user = null;
	private String login = null;

	public SessionAbstractBean() {
		super();
	}
	
	@PostConstruct
	public void initUser(){
		login = getExternalContext().getRemoteUser();

		if (login != null && !login.isEmpty()) {
			login = StringUtils.substringBefore(login, "@");
			prepareUser();
		}
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setMenu(MenuModel menu) {
		this.menu = menu;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public MenuModel getMenu() {
		return menu;
	}

	public void setUser(User usuario) {
		this.user = usuario;
	}

	public User getUser() {
		return user;
	}

	public void initSession(String login) {
		if (user == null) {
			this.login = login;
			prepareUser();
		}
	}

	public String accLogOut() throws IOException {
		getExternalContext().invalidateSession();
		getExternalContext().redirect(getExternalContext().getRequestContextPath());
		
		return null;
	}

	/**
	 * It load user from database. It store user into session and generates user
	 * menu.
	 */
	protected void prepareUser() {

		try {
			// Load user by valid login
			LoadUser cmd1 = (LoadUser) createCommand(LoadUser.class);
			cmd1.setIdObject(login);
			cmd1 = (LoadUser) runCommandOffSession(cmd1);
			setUser(cmd1.getResult());

			if (getUser() == null)
				setErrorMessage("Session Error:", cmd1.getUserComment());
			else
				putAuthUserIntoSession(getUser(), getCenter());
		} catch (CommandException ce) {
			logger.error(ce.getLocalizedMessage());
			setErrorMessage("Session Error:",
					"No se ha podido cargar al usuario, contacte con el servicio de informática");
		}
	}
}
