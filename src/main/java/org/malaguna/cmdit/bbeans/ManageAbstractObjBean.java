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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.ResultCommand;
import org.malaguna.cmdit.service.commands.generic.DeleteAbstractObjCmd;
import org.malaguna.cmdit.service.commands.generic.FindAbstractObjCmd;
import org.malaguna.cmdit.service.commands.generic.LoadAbstractObjCmd;
import org.malaguna.cmdit.service.commands.generic.SaveAbstractObjCmd;

public abstract class ManageAbstractObjBean<T extends AbstractObject<ID>, ID extends Serializable> extends RequestAbstractBean {
	private List<T> objectsList = null;
	private T fEdition = null;
	private T fSearch = null;
	private T object = null;
	private Class<T> clazzt;

	private Class<? extends LoadAbstractObjCmd<T, ID>> loadClazz;
	private String loadAuthAction = null;
	private String loadNavigation = null;

	private Class<? extends FindAbstractObjCmd<T, ID>> findClazz;
	private String findAuthAction = null;
	private String findNavigation = null;
	
	private Class<? extends SaveAbstractObjCmd<T, ID>> saveClazz;
	private Class<? extends DeleteAbstractObjCmd<T, ID>> deleteClazz;
	
	private String[] exprops = null;

	@SuppressWarnings("unchecked")
	public ManageAbstractObjBean(){
        Type type = getClass().getGenericSuperclass();
        
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            clazzt = (Class<T>) paramType.getActualTypeArguments()[0];

            try {
				fEdition = clazzt.newInstance();
				fSearch  = clazzt.newInstance(); 
			} catch (Exception e) {
				logError("err.dab.consError", e, this.getClass());
				e.printStackTrace();
			}
        }		
	}
	
	public T getObject() {
		return object;
	}

	public void setObject(T Object) {
		this.object = Object;
	}

	public void setObjectsList(List<T> objectsList) {
		this.objectsList = objectsList;
	}

	public List<T> getObjectsList() {
		return objectsList;
	}

	public void setfSearch(T fSearch) {
		this.fSearch = fSearch;
	}

	public T getfSearch() {
		return fSearch;
	}

	public void setfEdition(T fEdition) {
		this.fEdition = fEdition;
	}

	public T getfEdition() {
		return fEdition;
	}

	protected String[] getExprops() {
		return exprops;
	}

	protected void setExprops(String[] exprops) {
		this.exprops = exprops;
	}

	//***************************************
	// Setters para los pares Accion-Comando
	//***************************************

	protected void setLoadConf(String loadAuthAction, String loadNavigation, Class<? extends LoadAbstractObjCmd<T, ID>> loadClazz){
		this.loadAuthAction = loadAuthAction;
		this.loadNavigation = loadNavigation;
		this.loadClazz = loadClazz;
	}

	protected void setFindConf(String findAuthAction, String findNavigation, Class<? extends FindAbstractObjCmd<T, ID>> findClazz){
		this.findAuthAction = findAuthAction;
		this.findNavigation = findNavigation;
		this.findClazz = findClazz;
	}
	
	protected void setSaveConf(Class<? extends SaveAbstractObjCmd<T, ID>> saveClazz){
		this.saveClazz = saveClazz;
	}
	
	protected void setDeleteConf(Class<? extends DeleteAbstractObjCmd<T, ID>> deleteClazz){
		this.deleteClazz = deleteClazz;
	}

	//***************************************
	// Acciones propias del BackBean generico
	//***************************************

	/**
	 * It retrieve Entity from ActionEvent and update it to do
	 * proper work
	 *  
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void alObjectSelected(ActionEvent event) {
		this.object = (T) selectRowFromEvent(event.getComponent(), clazzt);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void alPrepareEdition(ActionEvent event){
		T aux = (T) selectRowFromEvent(event.getComponent(), clazzt);
		try {
			this.fEdition = (aux!=null)?loadObject(aux):clazzt.newInstance();
		}catch(Exception e){}
		loadRelations();
	}	
	
	/**
	 * Search entities by criteria and returns valid list
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void alFindObject(){
		FindAbstractObjCmd<T, ID> cmd = null;
		FindAbstractObjCmd<T, ID> aux = null;
		
		cmd = (FindAbstractObjCmd<T, ID>) createCommand(findClazz);
		cmd.setAction(findAuthAction);
		cmd.setObject(fSearch);
		
		cmd.setExclude(exprops);
		aux = completeFindCommand(cmd);
		cmd = (FindAbstractObjCmd<T, ID>) runCommand((aux != null)?aux:cmd);
		objectsList = cmd.getResult();
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String accPrepareEdition(){
		T aux = loadObject(fEdition);
		if(aux != null) fEdition = aux;
		loadRelations();
		return loadNavigation;
	}
	
	/**
	 * 
	 * @return
	 */
	public String accPrepareSearch(){
		loadRelations();
		return findNavigation;
	}
	
	/**
	 * Save or creates an entity
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void alSaveObject(){
		SaveAbstractObjCmd<T, ID> cmd = null;
		SaveAbstractObjCmd<T, ID> aux = null;
		
		cmd = (SaveAbstractObjCmd<T, ID>) createCommand(saveClazz);
		cmd.setObject(getfEdition());
		aux = completeSaveCommand(cmd);
		runCommand((aux != null)?aux:cmd);
		
		//Clean fEdition state
		try {fEdition = clazzt.newInstance();} catch (Exception e) {}
		
		alFindObject();
	}
	
	/**
	 * Delete an entity
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void alDeleteObject(){
		if(deleteClazz != null) {
			DeleteAbstractObjCmd<T, ID> cmd = (DeleteAbstractObjCmd<T, ID>) createCommand(deleteClazz);
			cmd.setObject(getfEdition());
			runCommand(cmd);

			//Clean fEdition state
			try {fEdition = clazzt.newInstance();} catch (Exception e) {}
		}else{
			logWarn("err.dab.noDeleteCommand", null, this.getClass());
		}
		
		alFindObject();
	}
	
	//***************************************
	// Funciones comunes de carga de datos
	//***************************************

	/**
	 * It re-loads from data base the object. If it doesn't exist
	 * it returns null.
	 *  
	 * @param obj to reload from database
	 * @return object reloaded or null.
	 */
	@SuppressWarnings("unchecked")
	protected T loadObject(T obj){
		T result = null;
		
		if( (obj != null) && (obj.getPid() != null) && (loadClazz != null) ){
			LoadAbstractObjCmd<T, ID> cmd = (LoadAbstractObjCmd<T, ID>) createCommand(loadClazz);
			cmd.setAction(loadAuthAction);
			cmd.setIdObject(obj.getPid());
			cmd = (LoadAbstractObjCmd<T, ID>)runCommand(cmd);
			result = cmd.getResult();
		}else{
			if(loadClazz == null){
				logError("err.dab.noCommandClass", null, clazzt, this.getClass());
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<SelectItem> loadAllObjectsAsSelectItems(Class<? extends ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>> clazz){
		List<SelectItem> result = null;
		ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>> cmd = null;
		
		cmd = (ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>) createCommand(clazz);
		cmd.setAction(findAuthAction);
		cmd = (ResultCommand<? extends List<? extends AbstractObject<? extends Serializable>>>) runCommand(cmd);
		
		if( (cmd != null) && (cmd.getResult() != null) && !(cmd.getResult().isEmpty()) ){
			result = new ArrayList<SelectItem>();
			for(AbstractObject<?> aux : cmd.getResult())
				result.add(new SelectItem(aux.getPid(), aux.toString()));
		}
		
		return result;
	}
	
	//***************************************
	// Extensión del comportamiento generico
	//***************************************

	protected abstract void loadRelations();
	protected abstract FindAbstractObjCmd<T, ID> completeFindCommand(FindAbstractObjCmd<T, ID> cmd);
	protected abstract SaveAbstractObjCmd<T, ID> completeSaveCommand(SaveAbstractObjCmd<T, ID> cmd);
}
