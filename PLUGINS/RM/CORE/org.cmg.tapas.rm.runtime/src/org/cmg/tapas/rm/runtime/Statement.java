/**
 * 
 */
package org.cmg.tapas.rm.runtime;

/**
 * @author loreti
 *
 */
public class Statement {
	
	private ModuleAction act;
	private Guard guard;
	private Command command;

	public Statement( ModuleAction act , Guard guard , Command command ) {
		this.act = act;
		this.guard = guard;
		this.command = command;
	}

	/**
	 * @return the act
	 */
	public ModuleAction getAct() {
		return act;
	}

	/**
	 * @return the guard
	 */
	public Guard getGuard() {
		return guard;
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

}
