/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

/**
 * @author loreti
 *
 */
public class CCSPChannel {
	
	private String name;
	private int code;
	
	public CCSPChannel( String name , int code ) {
		this.name = name;
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CCSPChannel) {
			return ((CCSPChannel) obj).code == code;
		}
		return false;
	}	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return code;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	
}
