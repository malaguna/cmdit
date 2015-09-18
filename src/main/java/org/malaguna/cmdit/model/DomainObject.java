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
package org.malaguna.cmdit.model;

import java.io.Serializable;

public class DomainObject<T extends Serializable> extends AbstractObject<T>{
	
	private static final long serialVersionUID = -2696180265041846999L;
	private String oid = null;

    public DomainObject(){
        oid = java.util.UUID.randomUUID().toString();
    }

	public String getOid() {
    	return oid;
    }
    
    public void setOid(String code) {
        this.oid = code;
    }

    @Override
    public int hashCode(){
        return getOid().hashCode();
    }
    
    @Override
    public boolean equals(Object obj){
        boolean result = false;
       
        result = (this == obj);
       
        if( (!result) && (obj != null))
            if(obj instanceof DomainObject<?>)
                result = this.getOid().equals(((DomainObject<?>)obj).getOid());
       
        return result;
    } 
}
