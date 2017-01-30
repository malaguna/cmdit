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
package org.malaguna.cmdit.bbeans;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.model.Center;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.BeanNames;
import org.malaguna.cmdit.service.CommandRunner;
import org.malaguna.cmdit.service.ServiceDelegate;
import org.malaguna.cmdit.service.commands.Command;
import org.malaguna.cmdit.service.commands.CommandException;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Clase abstracta con los atributos comunes a cualquier
 * Back Bean de la aplicación:
 *  - Acceso a la sesion
 *  - Acceso a la capa de servicio
 *  - Tratamiento común de errores en la lógica
 *  
 */
public class AbstractBean extends CommandRunner{
	private final static String AUTH_USER_KEY = "authuser";
	private Flash flash = null;
	
	/**
	 * Resuelve el locale del cliente
	 */
	public Locale getLocale(){
		return getFacesContext().getViewRoot().getLocale();
	}

	/**
	* Resuelve dinámicamente el ServiceDelegate
	*/
	public AbstractBean (){
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		super.setService((ServiceDelegate)wac.getBean(BeanNames.SERVICE));
	}

	/**
	 * 
	 * @return El contexto de Faces
	 */
	protected FacesContext getFacesContext(){
		return FacesContext.getCurrentInstance();
	}
	
	/**
	 * 
	 * @return El contexto del contenedor de Faces
	 */
	protected ExternalContext getExternalContext(){
		return getFacesContext().getExternalContext();
	}
	
	/**
	 * 
	 * @return El contexto del servlet, si es que se está ejecutanto en uno
	 */
	protected ServletContext getServletContext(){
		ServletContext sc = null;

		try{
			sc = (ServletContext)getExternalContext().getContext();
		}catch(Exception e){
			logError("err.abb.noServletContext", e, (Object[])null);
		}

		return sc;
	}
	
	/**
	 * Devuelve un back bean dado su nombre
	 * 
	 * @param backBean
	 */
	public Object getBackBeanReference(String backBean){
		ELContext context = getFacesContext().getELContext();
		return context.getELResolver().getValue(context, null, backBean);
	}
	
	/**
	 * Devuelve un Bean de Spring dado su nombre
	 * 
	 * @param name
	 * @return
	 */
	public Object getSpringBean(String name){
		Object result = null;
		
		if(getBeanFactory() != null)
			result = getBeanFactory().getBean(name);
		else{
			String className = this.getClass().getName();
			String mensaje = String.format("El MBean %s no ha sido inicializado convenientemente, no dispone de acceso al BeanFactory!", className);
			setErrorMessage("Error de inicialización", mensaje);
		}
		
		return result; 
	}	
	
	/**
	 * Introduce un mensaje de tipo INFO en la cola de mensajes de Faces
	 * @param summary Mensaje de Información
	 */
	protected void setInfoMessage(String summary, String msg) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, msg));
    }

	/**
	 * Introduce un mensaje de tipo INFO en la cola de mensajes de Faces asociado a un componente de la vista
	 * @param component El componente al que se le asocia el mensaje
	 * @param summary Mensaje de información
	 */
    protected void setInfoMessage(UIComponent component, String summary, String msg) {
        getFacesContext().addMessage(component.getClientId(getFacesContext()), new FacesMessage(FacesMessage.SEVERITY_INFO, summary, msg));
    }

    /**
	 * Introduce un mensaje de tipo WARM en la cola de mensajes de Faces
	 * @param summary Mensaje de Advertencia
	 */
	protected void setWarmMessage(String summary, String msg) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, msg));
    }

	/**
	 * Introduce un mensaje de tipo WARM en la cola de mensajes de Faces asociado a un componente de la vista
	 * @param component El componente al que se le asocia el mensaje
	 * @param summary Mensaje de Advertencia
	 */
    protected void setWarmMessage(UIComponent component, String summary, String msg) {
        getFacesContext().addMessage(component.getClientId(getFacesContext()), new FacesMessage(FacesMessage.SEVERITY_WARN, summary, msg));
    }

    /**
	 * Introduce un mensaje de tipo ERROR en la cola de mensajes de Faces
	 * @param summary Mensaje de error
	 */
	protected void setErrorMessage(String summary, String msg) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, msg));
    }

	/**
	 * Introduce un mensaje de tipo ERROR en la cola de mensajes de Faces asociado a un componente de la vista
	 * @param component El componente al que se le asocia el mensaje
	 * @param summary Mensaje de error
	 */
    protected void setErrorMessage(UIComponent component, String summary, String msg) {
        getFacesContext().addMessage(component.getClientId(getFacesContext()), new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, msg));
    }
	
    /**
	 * Introduce un mensaje de tipo FATAL en la cola de mensajes de Faces
	 * @param summary Mensaje de error fatal
	 */
	protected void setFatalMessage(String summary, String msg) {
        getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, msg));
    }

	/**
	 * Introduce un mensaje de tipo FATAL en la cola de mensajes de Faces asociado a un componente de la vista
	 * @param component El componente al que se le asocia el mensaje
	 * @param summary Mensaje de error fatal
	 */
    protected void setFatalMessage(UIComponent component, String summary, String msg) {
        getFacesContext().addMessage(component.getClientId(getFacesContext()), new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, msg));
    }

	/**
	 * It runs a command safely and catching all error info. If commands fail, 
	 * it won't do anything.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException
	 */
	protected Command runCommandWE(Command cmd) throws CommandException{
		cmd.setLocale(getLocale());
		cmd.setUser(getAuthUserFromSession());
		return super.runCommand(cmd);
	}
	
	/**
	 * It runs a command safely and catching all error info. If commands fail, 
	 * it will show error info standard way.
	 * 
	 * @param cmd
	 * @return
	 */
	@Override
	protected Command runCommand(Command cmd){
		cmd.setLocale(getLocale());
		cmd.setUser(getAuthUserFromSession());
		cmd.setCenter(getCenterFromSession());

		try{			
			cmd = super.runCommand(cmd);
			if( (cmd != null) && (cmd.getUserComment() != null) ){
				setInfoMessage("Command Info:", cmd.getUserComment());
			}
		}catch(Exception e){
			Throwable ce = (e.getCause() != null)?e.getCause():e;
			
			String aux = getMessage(ce.getClass().getName(), getLocale());
			String errMsg = null;
			
			if(aux != null)
				errMsg = String.format("%s: %s", aux, ce.getLocalizedMessage());
			else
				errMsg = ce.getLocalizedMessage();
			
			setErrorMessage("Command Error:", errMsg);
			cmd = null;
		}
		
		return cmd;
	}
	
	/**
	 * This is for bussines logic outside a request, in this case there is no possible user
	 * to retrieve from context. For example an HL7 message, a WebService without credentials,
	 * etc.
	 * 
	 * @param cmd
	 * @return
	 * @throws CommandException
	 */
	protected Command runCommandOffSession(Command cmd) throws CommandException{
		cmd.setLocale(getLocale());
		return super.runCommand(cmd);
	}
	
	/**
	 * Method that obtains the selected row from a UIComponent. It does not check super classes
	 * 
	 * @param anEvent
	 */
	protected Object selectRowFromEvent(UIComponent tmpComponent, Class<?> clazz) {
		return selectRowFromEvent(tmpComponent, clazz, false);
	}
	
	/**
	 * Method that obtains the selected row from a UIComponent. It can check super class
	 * 
	 * @param anEvent
	 */
	protected Object selectRowFromEvent(UIComponent tmpComponent, Class<?> clazz, boolean checkSuper) {
		Object tmpRowData = null;

		if(tmpComponent != null){
			while (null != tmpComponent && !(tmpComponent instanceof UIData))
				tmpComponent = tmpComponent.getParent();
	
			if (tmpComponent != null && (tmpComponent instanceof UIData)) {
				tmpRowData = ((UIData)tmpComponent).getRowData();
				
				boolean checked = clazz.isAssignableFrom(tmpRowData.getClass());
				
				if(!checked && checkSuper){
					checked = clazz.isInstance(tmpRowData);
				}
				
				if(!checked){
					setErrorMessage("Casting from event error:", "Error de casting para el objeto fila: se esperaba [" + clazz.toString() + "] y es [" + tmpRowData.getClass().toString() + "]");
					tmpRowData = null;
				}
			}
		}
		
		return tmpRowData;
	}
	
	/**
	 * 
	 */
	protected void sendFileThroughHttpResponse(File file, String fileName, String contentType) throws Exception {
		
		if(file != null && file.exists()){
			
			if(contentType != null){
				
				HttpServletResponse response = (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
				response.setHeader("pragma", "no-cache");
				response.setHeader("Cache-control",	"no-cache, no-store, must-revalidate");
				response.setHeader("Expires", "01 Apr 1995 01:10:10 GMT");
				
				StringBuffer contentDisposition = new StringBuffer();
				contentDisposition.append("attachment;");
				contentDisposition.append("filename=\"");
				contentDisposition.append(fileName!=null?fileName:file.getName());
				contentDisposition.append("\"");
				
				response.setHeader ("Content-Disposition", contentDisposition.toString());
				response.setContentType(contentType);
				
				try {
					FileUtils.copyFile(file, response.getOutputStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				getFacesContext().responseComplete();
			}else{
				setErrorMessage("Sendind file error:", "No se ha indicado tipo de contenido del fichero a enviar por HTTP");
			}
		}else{
			setErrorMessage("Sendind file error:", "No se ha indicado fichero para enviar por HTTP");
		}
	}
	
	/**
	 * It puts into session an user. Only a SessionAbstractBean can do this.
	 * 
	 * @param user
	 */
	protected void putAuthUserIntoSession(User user){
		if(this instanceof SessionAbstractBean){
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			ec.getSessionMap().put(AUTH_USER_KEY, user);
		}
	}
	
	/**
	 * It retrieves from session an user
	 */
	private User getAuthUserFromSession(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return (User)ec.getSessionMap().get(AUTH_USER_KEY);
	}
	
	private Center getCenterFromSession(){
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return (Center)ec.getSessionMap().get(AUTH_USER_KEY);
	}
	
	protected void putFlash(String key, Object value){
		
		if (flash == null){
			flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		}
		
		flash.put(key, value);

	}
	
	protected Object getFlash(String key){
		
		if (flash == null){
			flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		}
		
		return flash.get(key);

	}
	
	@SuppressWarnings("unchecked")
	protected List<SelectItem> loadAllObjectsAsSelectItems(Class<? extends ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>> clazz, String action){
		List<SelectItem> result = null;
		ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>> cmd = null;
		
		cmd = (ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>) createCommand(clazz);
		cmd.setAction(action);
		cmd = (ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>) runCommand(cmd);
		
		if( (cmd != null) && (cmd.getResult() != null) && !(cmd.getResult().isEmpty()) ){
			result = new ArrayList<SelectItem>();
			for(AbstractObject<?> aux : cmd.getResult())
				result.add(new SelectItem(aux.getPid(), aux.toString()));
		}
		
		return result;
	}

}
