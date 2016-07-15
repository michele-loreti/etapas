/**
 * 
 */
package org.cmg.tapas.core.util;

/**
 * @author loreti
 *
 */
public class NamedElement<S> {

	private String name;
	
	private S element;
	
	public NamedElement(String name , S element) {
		this.name = name;
		this.element = element;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the element
	 */
	public S getElement() {
		return element;
	}
	
}
