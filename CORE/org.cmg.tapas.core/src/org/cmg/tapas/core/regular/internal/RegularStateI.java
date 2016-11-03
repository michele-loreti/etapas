/**
 * 
 */
package org.cmg.tapas.core.regular.internal;

import java.util.Set;

/**
 * @author loreti
 *
 */
public interface RegularStateI<A> {

	public boolean isAccepting();
	
	public Set<? extends RegularStateI<A>> apply( A c ); 
	
	public Set<A> symbols();
	
}
