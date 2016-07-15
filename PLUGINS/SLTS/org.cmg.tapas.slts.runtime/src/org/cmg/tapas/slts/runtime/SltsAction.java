/**
 * 
 */
package org.cmg.tapas.slts.runtime;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * @author loreti
 *
 */
public class SltsAction implements ActionInterface {
	
	private String name;
	
	public SltsAction( String name ) {
		this.name = name;
	}

	@Override
	public int compareTo(ActionInterface o) {
		if (o instanceof SltsAction) {
			this.name.compareTo(((SltsAction) o).name);
		}
		return -1;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public boolean isTau() {
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
		if (obj instanceof SltsAction) {
			return this.name.equals( ((SltsAction) obj).name );
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
