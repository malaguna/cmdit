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
package org.malaguna.cmdit.service.commands.usrmgt;

import org.malaguna.cmdit.dao.RelatedCriteria;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.commands.generic.FindAbstractObjCmd;
import org.springframework.beans.factory.BeanFactory;

public class FindUser extends FindAbstractObjCmd<User, String> {

	public FindUser(BeanFactory bf) {
		super(bf);
		setLimit(100);
		setDao(getUserDao());
	}
	
	@Override
	protected RelatedCriteria[] completeRelations() {
		return null;
	}
}
