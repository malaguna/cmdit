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
package org.malaguna.cmdit.service;

import java.lang.reflect.Constructor;

import org.apache.log4j.Logger;
import org.malaguna.cmdit.service.commands.Command;
import org.malaguna.cmdit.service.commands.CommandException;
import org.springframework.beans.factory.BeanFactory;

public abstract class CommandRunner extends AbstractService{
	protected Logger logger = Logger.getLogger(this.getClass());
	private ServiceDelegate service = null;

	public ServiceDelegate getService() {
		return service;
	}

	public void setService(ServiceDelegate service) {
		this.service = service;
		setBeanFactory(service.getBeanFactory());
	}
	
	/**
	 * Dada una clase de un comando, instancia el mismo apropiadamente 
	 * 
	 * @param clazz
	 * @return
	 */
	protected Command createCommand(Class<? extends Command> clazz){
		Command comand = null;
		
		//Build a new command using Web Application Context (wac)
		try{
			Constructor<?> c = clazz.getConstructor(BeanFactory.class);
			comand = (Command)c.newInstance(getBeanFactory());
		}catch(Exception e){
			logger.error("Error creating new command [" + clazz.toString() + "] : " + e.getMessage());
		}

		return comand;
	}
	
	/**
	 * Ejecuta un commando de manera apropiada
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException
	 */
	protected Command runCommand(Command cmd) throws CommandException{
		return getService().runCommand(cmd);
	}
}
