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
package org.malaguna.cmdit.service.reflection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ReflectionUtils {
	private static ReflectionUtils instancia = null;
	private static HibernateProxyUtils hpu = null;

	protected ReflectionUtils(){
		super();
	}
	
	public static ReflectionUtils getInstance(){
		if(instancia == null){
			instancia = new ReflectionUtils();
			hpu = HibernateProxyUtils.getInstance();
		}
		
		return instancia;
	}
	
	public boolean compareAndUpdateAttributes(Object oldObj, Object newObj, String[] atributos, boolean update, StringBuffer msgBuffer){
		boolean result = true;
		
		if(atributos != null && atributos.length > 0){
			for(String attr : atributos){
				result &= compareAndUpdateAttribute(oldObj, newObj, attr, update, msgBuffer);
			}
		}
		
		return result;
	}
	
	public boolean compareAndUpdateAttribute(Object oldObj, Object newObj, String atributo, boolean update, StringBuffer msgBuffer){
		boolean result = false;
		
		if(oldObj != null && newObj != null){
			if(!isHibernateProxy(oldObj) && !isHibernateProxy(newObj)){
				PropertyDescriptor desc = null;
				
				try {
					String fisrtAttribute = null;
					String restAttribute = null;
					
					int pos = atributo.indexOf(".");					
					if(pos >= 0){
						fisrtAttribute = atributo.substring(0, pos);
						restAttribute = atributo.substring(pos + 1);
					}else{
						fisrtAttribute = atributo;
					}
					
					desc = PropertyUtils.getPropertyDescriptor(oldObj, fisrtAttribute);
					
					if(desc != null){											
						Object oldValue = desc.getReadMethod().invoke(oldObj);
						Object newValue = desc.getReadMethod().invoke(newObj);
						
						if(!isHibernateProxy(oldValue) && !isHibernateProxy(newValue)){
							String auxChangeMsg = null;
							
							result = (oldValue != null)?compareObjects(desc, oldValue, newValue):(newValue == null);				
							if(!result){
								if(msgBuffer != null){
									auxChangeMsg = buildChangeMessage(desc, fisrtAttribute, oldValue, newValue);
								}
								if(update){
									updateOldValue(oldObj, desc, oldValue, newValue);
								}					
							}
							
							if(msgBuffer != null)
								msgBuffer.append(getAppendMsg(auxChangeMsg, msgBuffer));
						}

						if(restAttribute != null)
							compareAndUpdateAttribute(oldValue, newValue, restAttribute, update, msgBuffer);
					}					
				} catch (NoSuchMethodException e) {
					String error = "Error in compareAndUpdateAttribute, class type [%s] has no property [%s]";
					throw new RuntimeException(String.format(error, oldObj.getClass(), atributo), e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	private String getAppendMsg(String auxChangeMsg, StringBuffer msgBuffer){
		String result = "";
		
		if(auxChangeMsg != null){
			result = (StringUtils.isEmpty(msgBuffer))?
						auxChangeMsg:
						String.format(", %s", auxChangeMsg);
		}
		
		return result;
	}
	
	private boolean isHibernateProxy(Object object){
		boolean result = (object != null)?hpu.isProxy(object):false;
		
		if(result){
			String error = "Object [%s] of class type [%s] is an hibernate proxy"
					+ " and thus it is not possible to compareAndUpdate by means"
					+ " of reflection. You must initializeAndUnproxy before.";
			throw new RuntimeException(String.format(error, object, object.getClass()));
		}
		
		return result;
	}
	
	private boolean compareObjects(PropertyDescriptor desc, Object orig, Object nuevo) {
		boolean result = false;
		
		if(Date.class.isAssignableFrom(desc.getPropertyType())){
			try {
				Method compare = Date.class.getMethod("compareTo", Date.class);
				int aux = (nuevo != null)?(int)compare.invoke((Date) orig, (Date) nuevo):-1;
				result = (aux == 0);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}else if(Collection.class.isAssignableFrom(desc.getPropertyType())) {			
			result = (nuevo != null)?
				CollectionUtils.isEqualCollection((Collection<?>) orig, (Collection<?>) nuevo):
				false;
		}else{
			result = orig.equals(nuevo);
		}
		
		return result;
	}
	
	private String buildChangeMessage(PropertyDescriptor desc, String atributo, Object oldValue, Object newValue){
		String result = null;
		
		if(Collection.class.isAssignableFrom(desc.getPropertyType())){
			StringBuffer msgBuff = null;
			Collection<?> oldSetAux = (Collection<?>)oldValue;
			Collection<?> newSetAux = (Collection<?>)newValue;
			
			if((oldSetAux != null && !oldSetAux.isEmpty()) ||
				(newSetAux != null && !newSetAux.isEmpty())){
				msgBuff = new StringBuffer("A la colección [" + atributo + "]");
			}
			
			if(oldSetAux != null && newSetAux != null){
				Collection<?> intersection = CollectionUtils.intersection(oldSetAux, newSetAux);
				Collection<?> nuevos = CollectionUtils.removeAll(newSetAux, intersection);
				Collection<?> borrados = CollectionUtils.removeAll(oldSetAux, intersection);
				
				if(nuevos != null && !nuevos.isEmpty()){
					msgBuff.append("Se añaden los siguientes elementos: ");
					for(Object element : nuevos)
						msgBuff.append(String.format("[%s], ", element.toString()));
					msgBuff.delete(msgBuff.length()-2, msgBuff.length());
				}
					
				if(borrados != null && !borrados.isEmpty()){
					msgBuff.append("Se eliminan los siguientes elementos: ");
					for(Object element : borrados)
						msgBuff.append(String.format("[%s], ", element.toString()));
					msgBuff.delete(msgBuff.length()-2, msgBuff.length());
				}
			}else if(oldSetAux != null && newSetAux == null){
				if(!oldSetAux.isEmpty()){
					msgBuff.append("Se eliminan los siguientes elementos: ");
					for(Object element : oldSetAux)
						msgBuff.append(String.format("[%s], ", element.toString()));
					msgBuff.delete(msgBuff.length()-2, msgBuff.length());
				}
			}else if(oldSetAux == null && newSetAux != null){
				if(!newSetAux.isEmpty()){
					msgBuff.append("Se añaden los siguientes elementos: ");
					for(Object element : newSetAux)
						msgBuff.append(String.format("[%s], ", element.toString()));
					msgBuff.delete(msgBuff.length()-2, msgBuff.length());
				}
			}
			
			if(msgBuff != null)
				result = msgBuff.toString();
		}else{
			String format = "['%s' :: (%s) -> (%s)]";
			result = String.format(format, atributo, 
				(oldValue != null) ? oldValue.toString() : "null", 
				(newValue != null) ? newValue.toString() : "null");
		}
		
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateOldValue(Object oldObj, PropertyDescriptor desc, Object oldValue, Object newValue){
		if(Collection.class.isAssignableFrom(desc.getPropertyType())){
			Collection oldSetAux = (Collection) oldValue;
			Collection newSetAux = (Collection) newValue;
			
			if(oldSetAux == null){
				setNewValue(oldObj, desc.getWriteMethod(), newSetAux);
			}else{
				oldSetAux.clear();
				
				if(newSetAux != null && !newSetAux.isEmpty()){
					oldSetAux.addAll(newSetAux);
				}
			}
		}else{
			setNewValue(oldObj, desc.getWriteMethod(), newValue);
		}
	}
	
	private void setNewValue(Object oldObj, Method method, Object newValue){
		try {
			method.invoke(oldObj, newValue);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}