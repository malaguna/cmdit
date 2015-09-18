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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;

public class AbstractService {
	private Logger logger = Logger.getLogger(this.getClass());
	private BeanFactory beanFactory = null;
	private MessageSource messages = null; 
	
	public AbstractService(){
	}
	
	public AbstractService(BeanFactory bf){
		setBeanFactory(bf);
	}
	
	public void setBeanFactory(BeanFactory bf) throws RuntimeException {
		if(bf != null){
			try{
				beanFactory = bf;
				messages = (MessageSource) getBeanFactory().getBean(BeanNames.MESSAGES);
			}catch(Exception e){
				logError("err.lgb.init", e, this.getClass());
				throw new RuntimeException(e);
			}
		}
	}
	
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	/**
	 * Extrae un mensaje del fichero de recursos para el idioma seleccionado
	 * @param mensaje Valor key del mensaje
	 */
    protected String getMessage(String mensaje, Object[] args, Locale locale){    
    	String result = null;
    	
    	try{
    		result = messages.getMessage(mensaje, args, locale);
    	}catch(Exception e){
    		logger.error(String.format("Couldn't find [%s] key message for Locale [%s]", mensaje, locale));
    		result = null;
    	}
    	
    	return result;
    }
    
    /**
     * Es un wrapper del método anterior para cuando no hay variables
     * en el mensaje.
     * 
     * @param mensaje
     * @param locale
     * @return
     */
    protected String getMessage(String mensaje, Locale locale){
    	String result = null;
    	
    	try{
    		result = getMessage(mensaje, null, locale);
    	}catch(Exception e){
    		logger.error(String.format("Couldn't find [%s] key message for Locale [%s]", mensaje, locale));
    		result = null;
    	}
    	
    	return result;
    }
    
	
	protected Logger getLogger(){
		return logger;
	}

	/**
	 * This method can get the appropriate message template and 
	 * applies arguments to generate a valid system error log.
	 */
	protected void logError(String key, Exception ex, Object ... arguments){
		if(ex == null)
			logger.error(messages.getMessage(key, arguments, Locale.getDefault()));
		else
			logger.error(messages.getMessage(key, arguments, Locale.getDefault()), ex);
	}

	/**
	 * This method can get the appropriate message template and 
	 * applies arguments to generate a valid system error log.
	 */
	protected void logWarn(String key, Exception ex, Object ... arguments){
		if(ex == null)
			logger.warn(messages.getMessage(key, arguments, Locale.getDefault()));
		else
			logger.warn(messages.getMessage(key, arguments, Locale.getDefault()), ex);
	}

	/**
	 * This method can get the appropriate message template and 
	 * applies arguments to generate a valid system error log.
	 */
	protected void logInfo(String key, Exception ex, Object ... arguments){
		if(ex == null)
			logger.info(messages.getMessage(key, arguments, Locale.getDefault()));
		else
			logger.info(messages.getMessage(key, arguments, Locale.getDefault()), ex);
	}

	/**
	 * This method can get the appropriate message template and 
	 * applies arguments to generate a valid system error log.
	 */
	protected void logDebug(String key, Exception ex, Object ... arguments){
		if(ex == null)
			logger.debug(messages.getMessage(key, arguments, Locale.getDefault()));
		else
			logger.debug(messages.getMessage(key, arguments, Locale.getDefault()), ex);
	}
}
