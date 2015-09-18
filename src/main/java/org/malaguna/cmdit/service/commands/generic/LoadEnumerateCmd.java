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
package org.malaguna.cmdit.service.commands.generic;

import java.util.List;

import org.malaguna.cmdit.dao.EnumerateDAO;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.beans.factory.BeanFactory;

public class LoadEnumerateCmd extends ResultCommand<List<String>> {
	private EnumerateDAO dao = null;
	private String enumerate = null;

	public LoadEnumerateCmd(BeanFactory bf) {
		super(bf);
	}
	
	public void setEnumerate(String enumerate) {
		this.enumerate = enumerate;
	}

	public String getEnumerate() {
		return enumerate;
	}

	protected EnumerateDAO getDao() {
		return dao;
	}

	protected void setDao(EnumerateDAO dao) {
		this.dao = dao;
	}

	@Override
	public boolean isValid(){
		return super.isValid() &&
			   enumerate != null &&
			   dao != null;
	}

	@Override
	public ResultCommand<List<String>> runCommand() throws Exception {
		this.setResult(dao.getValues(enumerate));
		return this;
	}
}
