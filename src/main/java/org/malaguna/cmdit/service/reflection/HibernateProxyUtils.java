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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.proxy.HibernateProxy;
import org.malaguna.cmdit.model.AbstractObject;

public class HibernateProxyUtils {
	private static HibernateProxyUtils instancia = null;
	
	protected HibernateProxyUtils(){
		super();
	}

	public static HibernateProxyUtils getInstance(){
		if(instancia == null){
			instancia = new HibernateProxyUtils();
		}
		
		return instancia;
	}
	
	public Object unproxy(Object object) {
		Object result = object;
		
		if (object != null && isProxy(object)) {
			try{
			Hibernate.initialize(object);
			HibernateProxy proxy = (HibernateProxy) object;
			result = proxy
				.getHibernateLazyInitializer()
				.getImplementation();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public boolean isProxy(Object object){
		return object instanceof HibernateProxy;
	}
	
	private boolean isDomainObject(Class<?> propertyClass) {
		return AbstractObject.class.isAssignableFrom(propertyClass);
	}

	private boolean isCollection(Class<?> propertyClass) {
		return Collection.class.isAssignableFrom(propertyClass);
	}
	
	public Object deepLoad(Object object, Object guideObj) {
		
		if(object != null){
			//unproxy if needed
			object = unproxy(object);

			//specific deep loading 
			if (isCollection(object.getClass())) {					
				object = deepLoadCollection((Collection<?>) object, (Collection<?>) guideObj);				
			}else if(isDomainObject(object.getClass())){
				deepLoadDomainObject((AbstractObject<?>) object, guideObj);
			}else{
				//throw new RuntimeException("Unsupported object unproxy");
			}
		}
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<?> deepLoadCollection(Collection<?> collection, Collection<?> guideObj) {
		Collection<Object> result = null;
		
		if(guideObj != null && !guideObj.isEmpty()){
			try {
				if (collection instanceof PersistentSet) {
					result = new HashSet<Object>();
				}else  if (collection instanceof PersistentList){
					result = new ArrayList<Object>();			
				} else {
					result = collection.getClass().newInstance();
				}
				
				//Recuperar primera instancia del guideObj y usarlo como siguiente guideObj
				Object collGuideObj = guideObj.iterator().next();				
				for (Object aux : collection) {
					result.add(deepLoad(aux, collGuideObj));
				} 
				
			} catch (Throwable e) {
				e.printStackTrace();
			} 
		}
		
		return result;
	}
	
	private void deepLoadDomainObject(AbstractObject<?> object, Object guideObj) {
		PropertyDescriptor[] properties = 
				PropertyUtils.getPropertyDescriptors(unproxy(object));

		for (PropertyDescriptor property : properties) {
			String pName = property.getName(); 
			
			if (PropertyUtils.isWriteable(object, pName) && 
				PropertyUtils.isReadable(object, pName)) {

				try {
					Object propGuideObject = property.getReadMethod().invoke(guideObj);
					
					if(null != propGuideObject){
						Object unproxied = deepLoad(property.getReadMethod().invoke(object), propGuideObject);
						property.getWriteMethod().invoke(object, unproxied);
					}
				} catch (IllegalAccessException  e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
			} 
		} 
	}	
}
