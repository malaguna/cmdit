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

public abstract class AbstractObject<T extends Serializable> implements Serializable, Comparable<Object>{

	private static final long serialVersionUID = 3351769226046044418L;
	private T pid = null;

    public void setPid(T code) {
		this.pid = code;
	}

	public T getPid() {
		return pid;
	}

	@Override 
	public String toString(){
		return (pid != null)?getClass() + " [" + pid + "]":super.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Object arg) {
		boolean esComparable = false;
		int result = -1;
				
		if(arg != null){
			
			//Intenta el casting			
			AbstractObject<T> aux = null;
			try{aux = this.getClass().cast(arg);}
			catch (Exception e) {/*no hacer nada*/}
			
			//Si el casting funcionó
			if(aux != null){
			
				//Recuperar las interfaces que implementa T y ver si alguna es Comparable
				Class<?>[] interfaces = getPid().getClass().getInterfaces();
				if(interfaces != null)
					for(int i = 0; (i < interfaces.length) && (!esComparable) ; i++)
						esComparable = interfaces[i].equals(Comparable.class);
						
				if(esComparable){				
					@SuppressWarnings("rawtypes")
					Comparable a = Comparable.class.cast(this.getPid());
					@SuppressWarnings("rawtypes")
					Comparable b = Comparable.class.cast(aux.getPid());
					result = a.compareTo(b);
				}
			}
		}
		
		return result;
	}
}