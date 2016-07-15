/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public abstract class RMSpecification {
	
	private StateEnumerator enumerator;
	private String[] variableNames;

	public RMSpecification( String[] variableNames , StateEnumerator enumerator ) {		
		this.enumerator = enumerator;
		this.variableNames = variableNames;
	}
	
	public StateEnumerator getEnumerator( ) {
		return enumerator;
	}
	
	public abstract AbstractReactiveModule getSystem( String name );
		
	public abstract int getValueOf( String constant );
	
	public int getStateValue( int[] state ) {
		return enumerator.enumerate(state);
	}
	
	public String[] getVariableNames() {
		return variableNames;
	}
	
	public abstract int getIndexOf( String name );
	
	
	public abstract String[] getSystems();

}
