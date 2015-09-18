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
package org.malaguna.cmdit.dao;

import java.io.Serializable;
import java.util.List;

public interface BasicDAO<T, ID extends Serializable> {

	/**
	 * Recupera un objeto por su identificador (PK)
	 * 
	 * @param id of the object loaded
	 * @param type class of the entity type
	 * @return
	 */
	public abstract T findById(ID id);

	/**
	 * Devuelve todas las instancias de un objeto
	 * 
	 * @param type
	 * @return
	 */
	public abstract List<T> findAll();

	/**
	 * Busca objetos por similitud a uno de ejempo
	 * 
	 * @param sample
	 * @return
	 */
	public abstract List<T> findByCriteria(T sample, String[] excludeProps,
			RelatedCriteria[] relations);

	/**
	 * Busca cuantos objetos que similares al criterio existen.
	 * 
	 * @param sample
	 * @return
	 */
	public abstract Long findByCriteriaCount(T sample, String[] excludeProps,
			RelatedCriteria[] relations);

	/** 
	 * persiste un objeto
	 * 
	 * @param obj
	 */
	public abstract void persist(T obj);

	/**
	 * Reasocia un objeto al contexto de persistencia
	 * 
	 * @param dirtyMode, permite elegir el modo de reasociar
	 * @param obj
	 */
	public abstract void reattach(T obj, int dirtyMode);

	/**
	 * Fusiona un objeto desasociado con la sesión
	 * 
	 * @param obj
	 * @return obj merged. Devuelve el objeto reasociado, no el original
	 */
	public abstract T merge(T obj);

	/**
	 * Borra una instancia de un objeto
	 * 
	 * @param obj
	 */
	public abstract void delete(T obj);

}