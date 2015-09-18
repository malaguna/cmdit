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
package org.malaguna.cmdit.service.ldap;

import java.util.Hashtable;
import java.util.Locale;

import javax.naming.*;
import javax.naming.directory.*;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

public class LDAPBase {

	// Propiedades para crear la conexión LDAP
	private String initContext = "com.sun.jndi.ldap.LdapCtxFactory";
	private String server;
	private String context;

	// Credenciales del usuario genérico para realizar la búsqueda
	private String user = null;
	private String password = null;

	// Configuración del comportamiento LDAP
	private boolean importUser = false;
	private boolean refreshUser = false;

	// bundle de mensajes y logger
	private MessageSource messages = null;
	private Logger logger = Logger.getLogger(this.getClass());

	public void setMessages(MessageSource messages) {
		this.messages = messages;
	}

	public Attributes loadUser(String uid) {
		return loadUser(uid, null);
	}
	
	public Attributes loadUser(String uid, String[] attrs) {

		// Preparar las variables de entorno para la conexión JNDI
		Hashtable<String, String> entorno = new Hashtable<String, String>();

		// Credenciales del usuario para realizar la búsqueda
		String cadena = "uid=" + user + "," + context;

		entorno.put(Context.PROVIDER_URL, server);
		entorno.put(Context.INITIAL_CONTEXT_FACTORY, initContext);
		if(password != null && user != null){
			entorno.put(Context.SECURITY_PRINCIPAL, cadena);
			entorno.put(Context.SECURITY_CREDENTIALS, password);
		}

		Attributes atributos = null;

		try {
			// Crear contexto de directorio inicial
			DirContext ctx = new InitialDirContext(entorno);

			// Recuperar atributos del usuario que se está buscando
			if(attrs != null)
				atributos = ctx.getAttributes("uid=" + uid + "," + context, attrs);
			else
				atributos = ctx.getAttributes("uid=" + uid + "," + context);
			
			// Cerrar la conexion
			ctx.close();
		} catch (NamingException e) {
			logger.error(messages.getMessage("err.ldap.attribute",
					new Object[] { e }, Locale.getDefault()));
		}

		return atributos;

	}

	public DirContext getDirContext() {
		DirContext ctx = null;
		String cadena = "uid=" + user + "," + context;
		Hashtable<String, String> entorno = new Hashtable<String, String>();
		
		entorno.put(Context.PROVIDER_URL, server);
		entorno.put(Context.SECURITY_PRINCIPAL, cadena);
		entorno.put(Context.SECURITY_CREDENTIALS, password);
		entorno.put(Context.INITIAL_CONTEXT_FACTORY, initContext);
		
		try {
			ctx = new InitialDirContext(entorno);
		} catch (NamingException e) {
			logger.error(messages.getMessage("err.ldap.attribute",
					new Object[] { e }, Locale.getDefault()));
		}
			
		return ctx;
	}

	/**
	 * @param servidor
	 *            the servidor to set
	 */
	public void setServer(String servidor) {
		this.server = servidor;
	}

	/**
	 * @return the servidor
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @param contexto
	 *            the contexto to set
	 */
	public void setContext(String contexto) {
		this.context = contexto;
	}

	/**
	 * @return the contexto
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUser(String usuario) {
		this.user = usuario;
	}

	/**
	 * @return the usuario
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	public void setImportUser(boolean importar) {
		this.importUser = importar;
	}

	public boolean isImportUser() {
		return importUser;
	}

	public void setRefreshUser(boolean refrescar) {
		this.refreshUser = refrescar;
	}

	public boolean isRefreshUser() {
		return refreshUser;
	}
}
