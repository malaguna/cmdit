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
import java.util.List;

import org.malaguna.cmdit.dao.usrmgt.LogDAO;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Log;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public class FindLogs extends ResultCommand<List<Log>> {
	private String usuario = null;
	private String accion = null;
	private Date fechaIni = null;
	private Date fechaFin = null;
	private LogDAO logDao = null;	
	private Integer limit = new Integer(1000);
	
	public FindLogs(BeanFactory bf) {
		super(bf);
		setAction(ActionHelper.MANAGE_USERS);
		logDao = (LogDAO)bf.getBean(BeanNames.LOG_DAO);
	}

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public void setUsuario(String userObj) {
		this.usuario = userObj;
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setAccion(String actObj) {
		this.accion = actObj;
	}

	public String getAccion() {
		return accion;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getLimit() {
		return limit;
	}

	@Override
	public boolean isValid(){
		return  super.isValid() &&
				logDao != null;
	}	

	@Override
	public ResultCommand<List<Log>> runCommand() {
		Long resultCount = null;
		
		if(limit != null)
			resultCount = logDao.findLogsCount(usuario, accion, fechaIni, fechaFin);
		
		if( (resultCount == null) || (resultCount <= limit) )
			this.setResult(logDao.findLogs(usuario, accion, fechaIni, fechaFin));
		else
			createUserComment("warn.faoc.limited", resultCount, limit);
		
		return this;
	}
}
