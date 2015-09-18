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

public class MasterObject<T extends Serializable> extends AbstractObject<T> {

	private static final long serialVersionUID = -7501761370475666734L;
	private String value = null;

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}	

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Object arg) {
		int result = -1;
				
		if(arg != null){
			
			//Intenta el casting			
			MasterObject<T> aux = null;
			try{aux = this.getClass().cast(arg);}
			catch (Exception e) {/*no hacer nada*/}

			if(aux != null)
				result = this.getValue().compareTo(aux.getValue());
		}
		
		return result;
	}

	@Override 
	public String toString(){
		return (getValue() != null)?getValue():super.toString();
	}
	
    @Override
    public boolean equals(Object obj){
        boolean result = false;
       
        result = (this == obj);
       
        if( (!result) && (obj != null))
            if(obj instanceof MasterObject<?>)
            	if (this.getValue() != null && ((MasterObject<?>)obj).getValue() != null)
            		result = this.getValue().equals(((MasterObject<?>)obj).getValue());
            	else 
            		result = super.equals(obj);
       
        return result;
    } 
    
    public int hashCode(){
    	
    	if (getValue() == null)
    		return super.hashCode();
    	else
    	    return getValue().hashCode();
    }
}
