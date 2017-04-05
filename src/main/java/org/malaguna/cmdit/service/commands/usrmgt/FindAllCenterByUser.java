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

import java.util.List;

import org.malaguna.cmdit.dao.usrmgt.ParticipationDAO;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Center;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public class FindAllCenterByUser extends ResultCommand<List<Center>> {
	private ParticipationDAO dao = null;
	private User usuario = null;

	public FindAllCenterByUser(BeanFactory bf) {
		super(bf);
		dao = (ParticipationDAO) bf.getBean(BeanNames.PARTICIPATION_DAO);
		setAction(ActionHelper.ASOCIATE_ROL_USERS);
	}
	
	@Override
	public boolean isValid(){
		return dao != null && usuario != null;
	}

	@Override
	public ResultCommand<List<Center>> runCommand() throws Exception {
		this.setResult(dao.findByUser(usuario));
		return this;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}
	
}
