/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public class Rule {
	
	private Guard guard;
	private Command command;

	public Rule( Guard guard , Command command ) {
		this.guard = guard;
		this.command = command;
	}
	
	public Guard getGuard( ) {
		return guard;
	}
	
	public Command getCommand( ) {
		return command;
	}
	
	public Rule combine( Rule r ) {
		return new Rule( new AndRule(guard, r.guard) , new ComposedCommand( command, r.command ) );
	}
	
}
