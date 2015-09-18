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

import java.io.Serializable;
import java.util.ArrayList;

import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.Command;
import org.springframework.beans.factory.BeanFactory;

/**
 * It is a wrapper of RemNMRelationListCmd. It receives only one id to add,
 * and it prepare a list with only one element.
 * 
 * It runs runCommand of super class.
 * 
 * @author miguel
 *
 * @param <T>
 * @param <I>
 * @param <S>
 * @param <J>
 */
public abstract class RemNMRelationCmd<T extends AbstractObject<I>, I extends Serializable, S extends AbstractObject<J>, J extends Serializable> extends RemNMRelationListCmd<T, I, S, J> {
	private J e2Id = null;

	public RemNMRelationCmd(BeanFactory bf) {
		super(bf);
	}
	
	public J getE2Id() {
		return e2Id;
	}

	public void setE2Id(J e2Id) {
		this.e2Id = e2Id;
	}

	@Override
	public boolean isValid(){
		setE2Ids(new ArrayList<J>());
		
		return  super.isValid() &&
				e2Id != null;
	}

	@Override
	public Command runCommand(){
		getE2Ids().add(e2Id);
		return super.runCommand();
	}
	
	protected abstract T deleteAssociation(T e1Obj, S e2Obj);
}