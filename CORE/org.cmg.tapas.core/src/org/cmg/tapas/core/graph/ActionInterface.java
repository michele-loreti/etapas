/*********************************************************************
  * FILE:  ActionInterface.java                                       *
  *                                                                   *
  * Copyright (C) 2006 Francesco Calzolai                             *
  *                                                                   *
  * This is free software; you can redistribute it and/or             *
  * modify it under the terms of the GNU General Public License       *
  * as published by the Free Software Foundation; either version 2    *
  * of the License, or (at your option) any later version.            *
  *                                                                   *
  * This software is distributed in the hope that it will be useful,  *
  * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the     *
  * GNU General Public License for more details.                      *
  *                                                                   *
  * You should have received a copy of the GNU General Public License *
  * along with JAgent; if not, write to the Free Software             *
  * Foundation, Inc., 51 Franklin St, Fifth Floor,                    *
  * Boston, MA  02110-1301  USA                                       *
  *                                                                   *
  *********************************************************************/
package org.cmg.tapas.core.graph;


/**
 * Interfaccia per il concetto di azione. L'interfaccia
 * <tt>GraphInterface</tt> si aspetta un'azione di questo tipo.
 * E' importante che le sottoclassi implementino correttamente
 * il metodo <tt>hashCode()</tt> in modo tale che se 
 * due oggetti risultano uguali abbiano lo stesso hashCode.
 * 
 * @see StateInterface
 * @see GraphInterface
 *  
 * @author Francesco Calzolai
 * @version 1.0 - 24 Nov 2005
 */
public interface ActionInterface 
extends Comparable<ActionInterface> {
	
	/**
	 * Restituisce il nome dell'azione.
	 * 
	 * @return Il nome dell'azione.
	 */
	String getId();	
	
	
	/**
	 * Dice se si tratta di una azione silente.
     * 
	 * @return <code>true</code> se questa � una azione silente.
	 */	
	boolean isTau();
	
//	public ActionInterface factory(String str);

}
