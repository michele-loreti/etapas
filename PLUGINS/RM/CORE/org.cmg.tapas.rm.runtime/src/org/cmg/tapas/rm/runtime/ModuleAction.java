/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * @author loreti
 *
 */
public class ModuleAction implements ActionInterface {

	private int id;
	
	private String name;
	
	public ModuleAction() {
		this("",0);
	}
	
	public ModuleAction(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public ModuleAction(String name) {
		this(name,-1);
	}

	@Override
	public int compareTo(ActionInterface o) {
		if (o instanceof ModuleAction) {
			return id-((ModuleAction) o).id;
		}
		return -1;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public boolean isTau() {
		return (id==0);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ModuleAction) {
			ModuleAction other = (ModuleAction) obj;
			return (this.id == other.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public String toString() {
		return name;
	}

	public int getActionIndex() {
		return this.id;
	}

	public void setActionIndex(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

}
