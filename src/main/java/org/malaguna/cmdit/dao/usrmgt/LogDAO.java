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
package org.malaguna.cmdit.dao.usrmgt;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.malaguna.cmdit.dao.impl.DomainHibernateDAOImpl;
import org.malaguna.cmdit.model.usrmgt.Log;

public class LogDAO extends DomainHibernateDAOImpl<Log, Long> {
	
	/**
	 * Recupera los logs de un usuario 
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Log> findLogs(String user, String act, Date fechaIni, Date fechaFin){
		List<Log> result = null;
		
		try{
			Criteria criteria = prepareCriteria(user, act, fechaIni, fechaFin);
			result = criteria.list();
		}catch(RuntimeException re){
			logErrMsg("findLogs", re);
			throw re;
		}
		
		return result;
	}
	
	public Long findLogsCount(String user, String act, Date fechaIni, Date fechaFin){
		Long result = null;
		List<?> aux = null;
		
		try{
			Criteria criteria = prepareCriteria(user, act, fechaIni, fechaFin);
			aux = criteria.setProjection(Projections.rowCount()).list();
			if( aux != null && !aux.isEmpty() )
				result = (Long)aux.get(0);
		}catch(RuntimeException re){
			logErrMsg("findLogsCount", re);
			throw re;
		}
		
		return result;
	}
	
	private Criteria prepareCriteria(String user, String act, Date fechaIni, Date fechaFin){
		Criteria criteria = this.getCurrentSession().createCriteria(Log.class);
		
		if(fechaIni != null)
			criteria.add(Property.forName("stamp").ge(fechaIni));
			
		if(fechaFin != null)
			criteria.add(Property.forName("stamp").le(fechaFin));
		
		if(user != null && !user.isEmpty())
			criteria
				.createCriteria("user")
				.add(Property.forName("pid").eq(user));
			
		if(act != null && !act.isEmpty())
			criteria.add(Property.forName("action").eq(act));
		
		return criteria;
	}
}