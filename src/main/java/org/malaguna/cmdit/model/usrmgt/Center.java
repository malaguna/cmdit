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

import java.util.Set;

import org.malaguna.cmdit.model.DomainObject;

public class Center extends DomainObject<Long> implements java.io.Serializable {
	private static final long serialVersionUID = -6292946976577590102L;
	private String name = null;
	private Boolean active = null;

	private Set<Participation> participations = null; 

	public Center() {
		super();
	}



	public Boolean getActive() {
		return active;
	}



	public void setActive(Boolean active) {
		this.active = active;
	}



	public Set<Participation> getParticipations() {
		return participations;
	}



	public void setParticipations(Set<Participation> participations) {
		this.participations = participations;
	}



	public String getName() {
		return name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}
	
	@Override 
	public String toString(){
		return (getPid() != null)?getName():super.toString();
	}
}
