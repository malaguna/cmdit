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

import java.util.Locale;

import org.malaguna.cmdit.service.commands.Command;
import org.malaguna.cmdit.service.commands.CommandException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class ServiceDelegate extends AbstractService implements BeanFactoryAware{
	private PlatformTransactionManager txManager = null;
	private Locale locale = new Locale("es", "es");
	
	public Locale getLocale(){
		return locale;
	}
	
	public PlatformTransactionManager getTxManager(){
		return txManager;
	}
	
	public void setTxManager(PlatformTransactionManager txManager){
		this.txManager = txManager;
	}
	
	private TransactionStatus getTxStatus(String action, boolean readOnly, String isolation, String propagation){
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		
		def.setName(action);
		
		//Set readOnly configuration
		def.setReadOnly(readOnly);
		
		//Set isolation configuration
		if(isolation.equals(Command.ISOLATION_DEFAULT)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_DEFAULT);
		}else if(isolation.equals(Command.ISOLATION_READ_COMMITED)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_COMMITTED);
		}else if(isolation.equals(Command.ISOLATION_READ_UNCOMMITED)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		}else if(isolation.equals(Command.ISOLATION_REPEATABLE_READ)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
		}else if(isolation.equals(Command.ISOLATION_SERIALIZABLE)){
			def.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_SERIALIZABLE);
		}

		//Set propagation configuration
		if(propagation.equals(Command.PROPAGATION_REQUIRED)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		}else if(propagation.equals(Command.PROPAGATION_MANDATORY)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_MANDATORY);
		}else if(propagation.equals(Command.PROPAGATION_NESTED)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_NESTED);
		}else if(propagation.equals(Command.PROPAGATION_NEVER)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_NEVER);
		}else if(propagation.equals(Command.PROPAGATION_NOT_SUPPORTED)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_NOT_SUPPORTED);
		}else if(propagation.equals(Command.PROPAGATION_REQUIRES_NEW)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		}else if(propagation.equals(Command.PROPAGATION_SUPPORTS)){
			def.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_SUPPORTS);
		}
		
		return txManager.getTransaction(def);
	}
	
	/**
	 * Recupera un mensaje del fichero de recursos y compone el mismo, en función de
	 * si hay o no excepción. Devuelve el contenido del mensaje.
	 * 
	 * @param cmd comando enejecución
	 * @param key clave del mensaje en el fichero de recursos
	 * @param e excepción producida
	 * @param args argumentos del mensaje del fichero de recursos
	 * @return mensja e de error formateado
	 */
	private String logMessage(Command cmd, String key, Exception e, Object ... args){
		String error = getMessage(key, args, getLocale());
		Object[] args2 = null;		
		String desc = null;
		
		if(e != null){
			args2 = new Object[] {cmd.getClass(), (cmd.getUser() != null)?cmd.getUser().getPid():"", error, e.getLocalizedMessage()};
			desc = getMessage("log.sdg.errorWE", args2, getLocale());
		}else{
			args2 = new Object[] {cmd.getClass(), (cmd.getUser() != null)?cmd.getUser().getPid():"", error};
			desc = getMessage("log.sdg.errorWOE", args2, getLocale());
		}
		
		getLogger().error(desc);
		return desc;
	}
	
	/**
	 * Este método comprueba la autorización del usuario para 
	 * ejecutar el comando en cuestión.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException 
	 */
	private boolean hasAuthorization(Command cmd) throws CommandException{
		TransactionStatus status = null;
		boolean result = false;
		
		try{
			//Check user authorization
			status = getTxStatus(cmd.getAction(), true, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			result = cmd.hasAuthorization();
			txManager.commit(status);
		}catch(Exception e){
			String msg = logMessage(cmd, "err.sdg.noAuth", e, cmd.getAction());

			//The roll back could also fail 
			try{
				txManager.rollback(status);
			}catch(RuntimeException re){
				logMessage(cmd, "err.sdg.noRollBack", re);
			}
			
			throw new CommandException(msg, e);
		}
		
		return result;
	}
	
	/**
	 * Este método inicia el logeo de la ejecución de un comando.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException 
	 */
	private boolean initLog(Command cmd) throws CommandException{
		TransactionStatus status = null;

		try{
			status = getTxStatus(cmd.getAction(), false, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			cmd.initLog();
			txManager.commit(status);
		}catch(Exception e){
			String msg = logMessage(cmd, "err.sdg.noLogI", e);

			//The roll back could also fail 
			try{
				txManager.rollback(status);
			}catch(RuntimeException re){
				logMessage(cmd, "err.sdg.noRollBack", re);
			}
			
			throw new CommandException(msg, e);
		}
		
		return true;
	}
	
	/**
	 * Este método finaliza el logeo de la ejecución de un comando.
	 * 
	 * @param cmd
	 */
	private void endLog(Command cmd){
		TransactionStatus status = null;

		try{
			//Log completion of the command
			status = getTxStatus(cmd.getAction(), false, Command.ISOLATION_DEFAULT, Command.PROPAGATION_REQUIRED);
			cmd.endLog();
			txManager.commit(status);
		}catch(Exception e){
			logMessage(cmd, "err.sdg.noLogE", e);
			
			//The roll back could also fail 
			try{
				txManager.rollback(status);
			}catch(RuntimeException re){
				logMessage(cmd, "err.sdg.noRollBack", re);
			}
		}
	}

	/**
	 * Este método ejecuta el comando. Captura las excepciones y las sirve a la 
	 * capa de vista.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException
	 */
	public Command runCommand(Command cmd) throws CommandException{
		TransactionStatus status = null;
		Command result = cmd;

		//Chechk command is well formed
		if( (cmd != null) && (cmd.isValid()) ){	
			if(hasAuthorization(cmd)){
				if(initLog(cmd)){
					
					try{
						status = getTxStatus(cmd.getAction(), cmd.isReadOnly(), cmd.getIsolation(), cmd.getPropagation());
						result = cmd.runCommand();
						txManager.commit(status);
					}catch(Exception e){
						String msg = logMessage(cmd, "err.sdg.runCommand", e);
						e.printStackTrace();

						//The roll back could also fail 
						try{
							txManager.rollback(status);
						}catch(RuntimeException re){
							logMessage(cmd, "err.sdg.noRollBack", re);
						}
						
						throw new CommandException(msg, e);
					}
					
					endLog(cmd);
				}
			}else{
				String msg = logMessage(cmd, "err.sdg.noUserAuth", null, cmd.getAction(), (cmd.getUser() != null)?cmd.getUser().getPid():"");
				throw new CommandException(msg);
			}
		}else{
			String msg = logMessage(cmd, "err.sdg.invalidCommand", null);
			throw new CommandException(msg);
		}
		
		return result;
	}
}
