/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public class AndRule implements Guard {
	
	private Guard g2;
	private Guard g1;

	public AndRule( Guard g1 , Guard g2 ) {
		this.g1 = g1;
		this.g2 = g2;
	}

	@Override
	public boolean eval(State state) {
		return g1.eval(state)&&g2.eval(state);
	}

}
