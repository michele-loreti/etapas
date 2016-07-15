package org.cmg.tapas.clts.runtime;

import java.util.Map;

import org.cmg.tapas.core.graph.ActionInterface;

public class CltsAction implements ActionInterface{
	
	private String name;
	
	public CltsAction( String name){
		this.name = name;
	}
	
	public CltsAction rename(Map<CltsAction, CltsAction> map){
		CltsAction a = map.get(this);
		if( a == null){
			return this;
		}
		return a;
	}
	
	@Override
	public int compareTo(ActionInterface o) {
		if (o instanceof CltsAction) {
				this.name.compareTo(((CltsAction) o).name);
		}
		return -1;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public boolean isTau() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
			return name.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CltsAction) {
			return this.name.equals( ((CltsAction) obj).name );
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

}
