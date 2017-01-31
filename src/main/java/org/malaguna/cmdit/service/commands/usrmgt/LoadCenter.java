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


import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Center;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.malaguna.cmdit.service.commands.generic.LoadAbstractObjCmd;
import org.springframework.beans.factory.BeanFactory;

public class LoadCenter extends LoadAbstractObjCmd<Center, String> {
	
	public LoadCenter(BeanFactory bf) {
		super(bf);
		setDao(getCenterDao());
		setAction(ActionHelper.LOAD_USER);

	}
	
	@Override
	public boolean isValid() {
		return getAction() != null && 
				getDao() != null && 
				getIdObject() != null;
	}

	@Override
	public boolean hasAuthorization(){
		return true;
	}
	
	@Override
	public ResultCommand<Center> runCommand() throws Exception {
		Center center = null;
		center = getCenterDao().findById(getIdObject());
			
		this.setResult(center);
		return this;
	}	
}
