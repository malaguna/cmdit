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

import org.malaguna.cmdit.model.AbstractObject;
import org.malaguna.cmdit.service.commands.generic.AddNMRelationCmd;
import org.malaguna.cmdit.service.commands.generic.AddNMRelationListCmd;
import org.malaguna.cmdit.service.commands.generic.FindAbstractObjCmd;
import org.malaguna.cmdit.service.commands.generic.LoadAbstractObjCmd;
import org.malaguna.cmdit.service.commands.generic.RemNMRelationCmd;

public abstract class ManageNMRelationBean<T extends AbstractObject<I>, I extends Serializable, S extends AbstractObject<J>, J extends Serializable> extends RequestAbstractBean {
	private List<S> candidates = null;
	private Object[] selection = null;
	private J idSelected = null;
	private S fSearch = null;
	private T object = null;
	private Class<T> clazzt;
	private Class<S> clazzs;
	
	private Class<? extends AddNMRelationListCmd<T, I, S, J>> asociateClazz;
	private Class<? extends RemNMRelationCmd<T, I, S, J>> disocciateClazz;
	private Class<? extends LoadAbstractObjCmd<T, I>> loadClazz;
	private Class<? extends FindAbstractObjCmd<S, J>> findClazz;

	private String prepareAuthAction = null;
	private String prepareNavigation = null;
	
	private String[] exprops = null;
	
	@SuppressWarnings("unchecked")
	public ManageNMRelationBean(){
        Type type = getClass().getGenericSuperclass();
        
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType)type;
            clazzt = (Class<T>) paramType.getActualTypeArguments()[0];
            clazzs = (Class<S>) paramType.getActualTypeArguments()[2];

            try {
				fSearch = clazzs.newInstance(); 
			} catch (Exception e) {
				logError("err.dab.consError", e, this.getClass());
				e.printStackTrace();
			}
        }		
		
	}

	public void setCandidates(List<S> result) {
		this.candidates = result;
	}

	public List<S> getCandidates() {
		return candidates;
	}

	public Object[] getSelection() {
		return selection;
	}

	public void setSelection(Object[] selecteds) {
		this.selection = selecteds;
	}

	public J getIdSelected() {
		return idSelected;
	}

	public void setIdSelected(J id) {
		this.idSelected = id;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public void setfSearch(S fSearch) {
		this.fSearch = fSearch;
	}

	public S getfSearch() {
		return fSearch;
	}

	public String[] getExprops() {
		return exprops;
	}

	public void setExprops(String[] exprops) {
		this.exprops = exprops;
	}

	//***************************************
	// Setters para los pares Accion-Comando
	//***************************************

	protected void setPrepareConf(String prepareAuthAction, String prepareNavigation, Class<? extends FindAbstractObjCmd<S, J>> prepareClazz){
		this.prepareAuthAction = prepareAuthAction;
		this.prepareNavigation = prepareNavigation;
		this.findClazz = prepareClazz;
	}	

	protected void setLoadClazz(
			Class<? extends LoadAbstractObjCmd<T, I>> loadClazz) {
		this.loadClazz = loadClazz;
	}

	protected void setAsociateClazz(
			Class<? extends AddNMRelationListCmd<T, I, S, J>> asociateClazz) {
		this.asociateClazz = asociateClazz;
	}

	protected void setDisocciateClazz(
			Class<? extends RemNMRelationCmd<T, I, S, J>> disocciateClazz) {
		this.disocciateClazz = disocciateClazz;
	}

	//***************************************
	// Funciones comunes de carga de datos
	//***************************************

	/**
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T loadObject(T obj){
		T result = null;
		
		if( (obj != null) && (loadClazz != null) ){
			LoadAbstractObjCmd<T, I> cmd = (LoadAbstractObjCmd<T, I>) createCommand(loadClazz);
			cmd.setAction(prepareAuthAction);
			cmd.setIdObject(obj.getPid());
			cmd = (LoadAbstractObjCmd<T, I>)runCommand(cmd);
			result = cmd.getResult();
			
		}else if(loadClazz != null){
			logError("err.dab.noCommandClass", null, clazzt, this.getClass());
		}
		
		return result;
	}

	//***************************************
	// Acciones propias del BackBean generico
	//***************************************

	/**
	 * 
	 * @return
	 */
	public String accPrepareEdition(){
		object = loadObject(object);
		return prepareNavigation;
	}
	
	@SuppressWarnings("unchecked")
	public String accFindCandidates(){
		FindAbstractObjCmd<S, J> cmd = null;
		
		object = loadObject(object);
		
		cmd = (FindAbstractObjCmd<S, J>) createCommand(findClazz);
		cmd.setAction(prepareAuthAction);
		cmd.setObject(fSearch);
		
		cmd.setExclude(exprops);
		cmd = (FindAbstractObjCmd<S, J>) runCommand(cmd);
		if( (cmd != null) && (cmd.getResult() != null) )
			candidates = deleteRelatedElements(cmd.getResult(), object);
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String accAsociateEntities(){
		AddNMRelationListCmd<T, I, S, J> cmd = null;
		List<J> auxLst = new ArrayList<J>();
		
		if(selection != null)
			for(Object auxObj : selection)
				auxLst.add(((S)auxObj).getPid());
		
		cmd = (AddNMRelationListCmd<T, I, S, J>) createCommand(asociateClazz);
		cmd.setE1Id(object.getPid());
		cmd.setE2Ids(auxLst);
		cmd = (AddNMRelationListCmd<T, I, S, J>) runCommand(cmd);
		
		return accPrepareEdition();
	}
	
	@SuppressWarnings("unchecked")
	public String accDisociateEntity(){
		RemNMRelationCmd<T, I, S, J> cmd = (RemNMRelationCmd<T, I, S, J>) createCommand(disocciateClazz);
		cmd.setE1Id(object.getPid());
		cmd.setE2Id(idSelected);
		cmd = (RemNMRelationCmd<T, I, S, J>)runCommand(cmd);

		return accPrepareEdition();
	}
	
	//***************************************
	// Extensión del comportamiento generico
	//***************************************

	protected abstract List<S> deleteRelatedElements(List<S> allElements, T object);
	protected abstract AddNMRelationCmd<T, I, S, J> completeAddRelationCommand(AddNMRelationCmd<T, I, S, J> cmd);
}