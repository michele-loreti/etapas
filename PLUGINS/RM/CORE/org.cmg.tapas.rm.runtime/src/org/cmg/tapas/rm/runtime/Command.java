/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public abstract class Command {
	
	protected abstract int apply( State current , int temp );

	public final State apply( State state ) {
		return state.getState( apply( state , state.state ) );
	}

}
