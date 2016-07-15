/**
 * 
 */
package org.cmg.tapas.extensions;

import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public interface TAPAsElementViewProvider {

	public TAPAsGraphElementContentProvider getContentProvider();
	
	public TAPAsGraphElementLabelProvider getLabelProvier();
	
	public Object getInput( String elementName );
	
	public String[] getElements();

	public void setModel(EObject model);

}
