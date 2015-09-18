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
package org.malaguna.cmdit.bbeans.usrmgt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.malaguna.cmdit.bbeans.RequestAbstractBean;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Log;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.commands.usrmgt.FindLogs;
import org.malaguna.cmdit.service.commands.usrmgt.FindUser;

public class AuditMBean extends RequestAbstractBean implements Serializable {
	private static final long serialVersionUID = -2975973343474146765L;

	private String usuario = null;
	private String accion = null;
	private Date fInicial = null;
	private Date fFinal = null;

	private User usrSearch = new User();
	private List<User> usrResult = null;

	private List<Log> logResult = null;
	private List<SelectItem> accList = null;

	public AuditMBean() {
		// Prepara la lista de acciones en formato SelectItem
		ActionHelper actHelper = (ActionHelper) getSpringBean(BeanNames.ACTION_HELPER);
		Iterator<String> it = actHelper.getActionSet().iterator();
		if (it != null) {
			accList = new ArrayList<SelectItem>();

			while (it.hasNext()) {
				String value = it.next();
				accList.add(new SelectItem(value, value));
			}
		}
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Date getfInicial() {
		return fInicial;
	}

	public void setfInicial(Date fInicial) {
		this.fInicial = fInicial;
	}

	public Date getfFinal() {
		return fFinal;
	}

	public void setfFinal(Date fFinal) {
		this.fFinal = fFinal;
	}

	public User getUsrSearch() {
		return usrSearch;
	}

	public void setUsrSearch(User usrSearch) {
		this.usrSearch = usrSearch;
	}

	public List<User> getUsrResult() {
		return usrResult;
	}

	public void setUsrResult(List<User> usrResult) {
		this.usrResult = usrResult;
	}

	public List<Log> getLogResult() {
		return logResult;
	}

	public void setLogResult(List<Log> logResult) {
		this.logResult = logResult;
	}

	public List<SelectItem> getAccList() {
		return accList;
	}

	public void setAccList(List<SelectItem> accList) {
		this.accList = accList;
	}

	public void alFindLogs(ActionEvent event) {
		FindLogs cmd = null;

		cmd = (FindLogs) createCommand(FindLogs.class);
		cmd.setUsuario(usuario);
		cmd.setAccion(accion);
		cmd.setFechaIni(fInicial);
		cmd.setFechaFin(fFinal);
		cmd = (FindLogs) runCommand(cmd);

		if (cmd != null)
			setLogResult(cmd.getResult());
		else
			setInfoMessage("Información",
					"La búsqueda no ha producido ningún resultado");
	}

	public void alCleanChooseUserForm() {
		usrSearch = new User();
		usrResult = null;
		usuario = null;
	}
	
	public void alFindUser(ActionEvent event) {
		FindUser cmd = null;

		cmd = (FindUser) createCommand(FindUser.class);
		cmd.setAction(ActionHelper.MANAGE_USERS);
		cmd.setObject(usrSearch);
		cmd = (FindUser) runCommand(cmd);

		if (cmd != null)
			setUsrResult(cmd.getResult());
		else
			setInfoMessage("Información",
					"La búsqueda no ha producido ningún resultado");
	}

	public void selectUserFromDialog(User user) {
		if(user != null)
			usuario = user.getPid();
	}
}