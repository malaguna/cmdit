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

import java.util.Date;

import org.malaguna.cmdit.model.DomainObject;

public class Log extends DomainObject<Long> implements java.io.Serializable {
	private static final long serialVersionUID = -8969909205931712471L;
	private Date stamp = new Date();
	private String comment = "";

	//Asociaciones
	private User user = null;
	private String action = null;
	
	public Log() {
		super();
		this.stamp = new Date();
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String accion) {
		this.action = accion;
	}

	public void setUser(User usuario) {
		this.user = usuario;
	}

	public User getUser() {
		return user;
	}

	public Date getStamp() {
		return this.stamp;
	}

	public void setStamp(Date stamp) {
		this.stamp = stamp;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String objeto) {
		this.comment = objeto;
	}
}
