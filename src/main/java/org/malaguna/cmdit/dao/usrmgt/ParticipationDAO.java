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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.malaguna.cmdit.dao.impl.DomainHibernateDAOImpl;
import org.malaguna.cmdit.model.Center;
import org.malaguna.cmdit.model.usrmgt.Participation;
import org.malaguna.cmdit.model.usrmgt.User;

public class ParticipationDAO extends DomainHibernateDAOImpl<Participation, String> {
	
	@SuppressWarnings("unchecked")
	public List<Participation> findByUserCenter(User user, Center center) {
		Criteria criteria =	getCurrentSession().createCriteria(persistentClass);

		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.eq("center", center));
		
		return (List<Participation>) criteria.list();
	}
}
