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
import java.util.HashSet;
import java.util.Set;

import org.malaguna.cmdit.model.DomainObject;


public class User extends DomainObject<String> implements java.io.Serializable {
	public static final User fullLoadGuide = new User();
	private static final long serialVersionUID = -6292946976577590102L;
	private String surName = null;
	private String name = null;
	private Date date = null;
	private Boolean active = null;
	private Center default_center = null;

	private Set<Participation> participations = null;
	
	public void init(){
		Participation.fullLoadGuide.init();
		fullLoadGuide.setParticipations(new HashSet<Participation>());
		fullLoadGuide.getParticipations().add(Participation.fullLoadGuide);
		fullLoadGuide.setDefault_center(new Center());
	}

	public User() {
		super();
	}
	
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date fecAlta) {
		this.date = fecAlta;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getActive() {
		return active;
	}
	
	public Center getDefault_center() {
		return default_center;
	}

	public void setDefault_center(Center defaultCenter) {
		this.default_center = defaultCenter;
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

	public String getSurName() {
		return surName;
	}

	public void setSurName(String apellidos) {
		this.surName = apellidos;
	}
	
	public String getFullName(){
		String result = null;
		
		if(getName() != null){
			result = getName();
			if(getSurName() != null)
				result += " " + getSurName();
		}
		
		return result;
	}

	public String getTrimmedFullName(){
		String result = getFullName();
		
		if(result != null && result.length() >= 20)
			result = result.substring(0, 19) + " ...";
		
		return result;
	}

	/**
	 * This is not convenience method, it is in Rol class, because the
	 * inverse is in User mapping
	 * 
	 * @param rol
	 */
	/*
	public void addRol(String rol){
		if(rol != null){
			if(roles == null)
				roles = new HashSet<String>();
			
			roles.add(rol);
		}
	}
	*/

	/**
	 * This is not convenience method, it is in Rol class, because the
	 * inverse is in User mapping
	 * 
	 * @param rol
	 */
	/*
	public void delRol(String rol){
		if(rol != null){
			if(roles != null)
				roles.remove(rol);
		}
	}
	
	public boolean hasRoleId(String rolId){
		boolean result = false;
		
		if(rolId != null && roles != null && !roles.isEmpty()){
			Iterator<String> roles = this.roles.iterator();
			
			while(!result && roles.hasNext()){
				String rol = roles.next();
				result = rolId.equals(rol);
			}
		}
		
		return result;
	}
	*/
	@Override 
	public String toString(){
		return (getPid() != null)?getFullName():super.toString();
	}
}
