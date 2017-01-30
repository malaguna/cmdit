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
package org.malaguna.cmdit.service.commands;

import java.util.Iterator;
import java.util.Locale;

import org.malaguna.cmdit.dao.usrmgt.LogDAO;
import org.malaguna.cmdit.dao.usrmgt.UserDAO;
import org.malaguna.cmdit.model.usrmgt.ActionHelper;
import org.malaguna.cmdit.model.usrmgt.Center;
import org.malaguna.cmdit.model.usrmgt.Log;
import org.malaguna.cmdit.model.usrmgt.Participation;
import org.malaguna.cmdit.model.usrmgt.RoleHelper;
import org.malaguna.cmdit.model.usrmgt.User;
import org.malaguna.cmdit.service.AbstractService;
import org.malaguna.cmdit.service.BeanNames;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;

public abstract class Command extends AbstractService {
	private Locale locale = new Locale("es", "es");

	public static final String ISOLATION_DEFAULT = "iso_default";
	public static final String ISOLATION_SERIALIZABLE = "iso_repeatable";
	public static final String ISOLATION_READ_COMMITED = "iso_commited";
	public static final String ISOLATION_READ_UNCOMMITED = "iso_uncommited";
	public static final String ISOLATION_REPEATABLE_READ = "iso_repeatable";

	public static final String PROPAGATION_REQUIRED = "prop_required";
	public static final String PROPAGATION_REQUIRES_NEW = "porp_new";
	public static final String PROPAGATION_MANDATORY = "prop_mandatory";
	public static final String PROPAGATION_NESTED = "prop_nested";
	public static final String PROPAGATION_NEVER = "prop_never";
	public static final String PROPAGATION_SUPPORTS = "prop_supports";
	public static final String PROPAGATION_NOT_SUPPORTED = "prop_not_supported";

	// Configuración de la transaccionalidad
	private String propagation = PROPAGATION_REQUIRED;
	private String isolation = ISOLATION_DEFAULT;
	private boolean readOnly = true;

	// Otros atributos
	private MessageSource messages = null;
	private String userComment = null; // Comentario de IU
	private String logComment = null; // Comentario de auditoria
	private String action = null;
	private boolean canLog = false;
	private User user = null;
	private Center center = null;
	private Log log = null;

	// DAO's
	private UserDAO userDao = null;
	private LogDAO logDao = null;

	// Helpers
	private ActionHelper actionHelper = null;
	private RoleHelper roleHelper = null;

	/**
	 * It creates a new command and retrieves user business logic
	 * 
	 * @param bf
	 */
	public Command(BeanFactory bf) {
		super(bf);

		try {
			actionHelper = (ActionHelper) bf.getBean(BeanNames.ACTION_HELPER);
			roleHelper = (RoleHelper) bf.getBean(BeanNames.ROLE_HELPER);
			messages = (MessageSource) bf.getBean(BeanNames.MESSAGES);
			userDao = (UserDAO) bf.getBean(BeanNames.USER_DAO);
			logDao = (LogDAO) bf.getBean(BeanNames.LOG_DAO);
		} catch (Exception e) {
			logError("err.cmd.init", e, this.getClass().toString());
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	protected void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getIsolation() {
		return isolation;
	}

	protected void setIsolation(String isolation) {
		this.isolation = isolation;
	}

	public String getPropagation() {
		return propagation;
	}

	protected void setPropagation(String propagation) {
		this.propagation = propagation;
	}

	protected UserDAO getUserDao() {
		return userDao;
	}

	protected LogDAO getLogDao() {
		return logDao;
	}

	protected String getLogComment() {
		return logComment;
	}

	protected void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public String getUserComment() {
		return userComment;
	}

	
	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		if (this.action == null && action != null)
			this.action = action;
		else
			logError("err.cmd.setAction", null, this.getClass().toString(),
					action, this.action);
	}

	protected void overrideAction(String action) {
		this.action = action;
	}

	public boolean getCanLog() {
		return canLog;
	}

	protected void setCanLog(boolean cl) {
		this.canLog = cl;
	}

	public ActionHelper getActionHelper() {
		return actionHelper;
	}

	public void setActionHelper(ActionHelper actionHelper) {
		this.actionHelper = actionHelper;
	}

	public RoleHelper getRoleHelper() {
		return roleHelper;
	}

	public void setRoleHelper(RoleHelper rolHelper) {
		this.roleHelper = rolHelper;
	}

	/**
	 * This method can get the appropriate message template and applies
	 * arguments to generate a valid localized message.
	 * 
	 * @param arguments
	 * @param template
	 */
	protected void createLogComment(String key, Object... arguments) {
		logComment = messages.getMessage(key, arguments, getLocale());
	}

	/**
	 * This method can get the appropriate message template and applies
	 * arguments to generate a valid localized message.
	 * 
	 * @param arguments
	 * @param template
	 */
	protected void createUserComment(String key, Object... arguments) {
		userComment = messages.getMessage(key, arguments, getLocale());
	}

	/**
	 * Whether the user has permission to run the action of the command
	 * 
	 * @return
	 */
	public boolean hasAuthorization() {
		boolean result = false;

		if (actionHelper != null) {
			if (actionHelper.exists(action)) {
				
				Iterator<Participation> iPart = user.getParticipations().iterator();
				Participation p = null;
				if(center != null){
					while(!result && (iPart.hasNext())){
						p = iPart.next();
						if(Long.parseLong(p.getCenter().getPid())==Long.parseLong(center.getPid())){
							result = roleHelper.isAuthorized(action, p.getRol());
							center = p.getCenter();
						}
					}
				}else{
					while (!result && (iPart.hasNext())){
						p = iPart.next();
						result = roleHelper.isAuthorized(action, p.getRol());
					}
					center = user.getDefaultCenter();
				}

			} else {
				logError("err.cmd.authNoAction", null, this.getClass()
						.toString());
			}
		} else {
			logError("err.cmd.authNoActionHelper", null);
		}

		return result;
	}

	/**
	 * Whether the command is well formed and ready to run. It must be overwrite
	 * to add parameter's validation of specialized commands
	 * 
	 * @return
	 */
	public boolean isValid() {
		return action != null && userDao != null && user != null;
	}

	/**
	 * Log that the user is trying to do something.
	 */
	public void initLog() {
		if ((action != null) && (canLog)) {
			log = new Log();
			log.setUser(user);
			log.setComment(messages.getMessage("log.cmd.trying", null,
					getLocale()));
			log.setAction(action);

			logDao.persist(log);
		}
	}

	/**
	 * Log the user do the action successfully. If there is no comment it puts
	 * complete action
	 */
	public void endLog() {
		if ((action != null) && (log != null)) {
			if (logComment == null)
				logComment = messages.getMessage("log.cmd.ended", null,
						getLocale());

			log.setComment(logComment);
			logDao.persist(log);
		}
	}

	/**
	 * Run the command. It must set the result and a log and user comment.
	 * 
	 * @return
	 */
	public abstract Command runCommand() throws Exception;
}
