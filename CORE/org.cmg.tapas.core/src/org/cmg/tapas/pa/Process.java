/**
 * Copyright (c) 2012 Concurrency and Mobility Group.
 * Universita' di Firenze
 *      
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Michele Loreti
 */
package org.cmg.tapas.pa;

import java.util.HashMap;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * @author loreti
 *
 */
public interface Process<S extends Process<S,A>,A extends ActionInterface> {

	public HashMap<A, Set<S>> getNext();

	public boolean isDeadlock();
	
}
