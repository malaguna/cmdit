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
package org.malaguna.cmdit.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.malaguna.cmdit.dao.BasicDAO;
import org.malaguna.cmdit.dao.RelatedCriteria;
import org.springframework.context.MessageSource;

/**
 * This class performs generic actions on persistent POJO 
 * instances. 
 * 
 * @author miguel
 *
 */
public abstract class BasicHibernateDAOImpl<T, ID extends Serializable> implements BasicDAO<T, ID> {
	public static final int DIRTY_EVALUATION = 1;
	public static final int DIRTY_IGNORE = 2;

	private Logger logger = Logger.getLogger(this.getClass());
	protected Class<T> persistentClass = null;
    private MessageSource messages = null;
    
    private SessionFactory sessionFactory; 
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	protected Session getCurrentSession(){
		return this.getSessionFactory().getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public BasicHibernateDAOImpl(){
		super();
		this.persistentClass = (Class<T>)
			((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}
	
	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}
	
	/**
	 * Logea un error mostrando información importante para su resolución.
	 * 
	 * @param obj
	 * @param method
	 * @param e
	 */
	protected void logErrMsg(String method, Exception e){
		String typeObjt = persistentClass.getClass().getName();
		Object[] args = {method, typeObjt, e.getLocalizedMessage()};
		logger.error(messages.getMessage("log.daoGen.error", args, null));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T findById(ID id){
		T result = null;
		
		//Avoid bad parameters
		if(id != null){
			
			try{
				result = (T) this.getCurrentSession().get(persistentClass, id);
			}catch (RuntimeException re){
				logErrMsg("findById", re);
				throw re;
			}
		}
		
		return result;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(){
		
		List<T> result = null;
		
		try{
			Criteria criteria = getCurrentSession().createCriteria(persistentClass);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			result = criteria.list();			
		}catch (RuntimeException re){
			logErrMsg("findAll", re);
			throw re;
		}
		
		return result;		
	}	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(T sample, String[] excludeProps, RelatedCriteria[] relations){
		List<T> result = null;
		
		Criteria criteria = prepareCriteria(sample, excludeProps, relations);
			
		try{
			result = criteria.list();
		}catch (RuntimeException re){
			logErrMsg("findByCriteria", re);
			throw re;
		}
		
		return result;		
	}
	
	@Override
	public Long findByCriteriaCount(T sample, String[] excludeProps, RelatedCriteria[] relations){
		Long result = null;
		List<?> aux = null;
			
		Criteria criteria = prepareCriteria(sample, excludeProps, relations);
		criteria.setProjection(Projections.rowCount());
		
		try{
			aux = criteria.list();
			if( aux != null && !aux.isEmpty() )
				result = (Long) aux.get(0);
		}catch (RuntimeException re){
			logErrMsg("findByCriteriaCount", re);
			throw re;
		}
			
		return result;		
	}	
	
	@Override
	public void persist(T obj){
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				this.getCurrentSession().saveOrUpdate(obj);
			}catch (RuntimeException re){
				logErrMsg("persist", re);
				throw re;
			}
		}
	}
	
	@Override
	public void reattach(T obj, int dirtyMode){
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				switch(dirtyMode){
		
					case DIRTY_EVALUATION :
						this.getCurrentSession().update(obj);
						break;
						
					case DIRTY_IGNORE :
						this.getCurrentSession().buildLockRequest(null).setLockMode(LockMode.NONE).lock(obj);
						break;
				}
			}catch(RuntimeException re){
				logErrMsg("reattach", re);
				throw re;
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T merge(T obj){
		T result = null;
		
		//Avoid bad parameters
		if( (obj != null) ){
			try{
				result = (T)this.getCurrentSession().merge(obj);
			}catch (RuntimeException re){
				logErrMsg("merge", re);
				throw re;
			}
		}
		
		return result;
	}
	
	@Override
	public void delete(T obj){

		//Avoid bad parameters
		if( (obj != null) ){
			try{
				this.getCurrentSession().delete(obj);
			}catch (RuntimeException re){
				logErrMsg("delete", re);
				throw re;
			}
		}
	}
	
	private Criteria prepareCriteria(T sample, String[] excludeProps, RelatedCriteria[] relations){
		Criteria result = null;
		
		if(sample == null){
			result = this.getCurrentSession().createCriteria(persistentClass);
		}else{		
			Example example = Example.create(sample);
			example.enableLike(MatchMode.ANYWHERE);
			example.ignoreCase();
			
			if(excludeProps != null)
				for(String prop : excludeProps)
					example.excludeProperty(prop);
			
			result = this.getCurrentSession().createCriteria(persistentClass);
			result.add(example);
			
			//Si existen objetos relacionados
			if(relations != null){
				for(RelatedCriteria relation : relations){
					if( (relation != null) && (relation.isValid()) ){
						
						//Se crea el ejemplo del objeto relacionado
						Example objectEx = Example.create(relation.getObject());
						objectEx.enableLike(MatchMode.ANYWHERE);
						objectEx.ignoreCase();
					
						//Se añade el join a la búsqueda
						result.createCriteria(relation.getName())
							.add(objectEx);
					}
				}
			}
		}
		
		return result;
	}
}
