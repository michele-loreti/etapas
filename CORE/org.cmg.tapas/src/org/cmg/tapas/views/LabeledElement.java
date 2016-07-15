/**
 * 
 */
package org.cmg.tapas.views;

/**
 * @author loreti
 *
 */
public class LabeledElement {
	
	private String name;
	
	private Object element;

	public LabeledElement( String name , Object element ) {
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
	public Object getElement() {
		return element;
	}

}
