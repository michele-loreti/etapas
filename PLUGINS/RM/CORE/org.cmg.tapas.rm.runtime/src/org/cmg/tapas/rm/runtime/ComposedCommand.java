/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public class ComposedCommand extends Command {

	private Command c1;
	private Command c2;


	public ComposedCommand( Command c1 , Command c2 ) {
		this.c1 = c1;
		this.c2 = c2;
	}


	@Override
	protected int apply( State current , int tmp) {
		return c2.apply( current , c1.apply( current , tmp ) );
	}
	
	
}
