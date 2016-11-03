/**
 * 
 */
package org.cmg.tapas.rm.extensions;

import org.cmg.tapas.rm.xtext.module.Model;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class TAPAsElementViewProvider implements org.cmg.tapas.extensions.TAPAsElementViewProvider {

	private Model model;

	/**
	 * 
	 */
	public TAPAsElementViewProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsElementViewProvider#getContentProvider()
	 */
	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsElementViewProvider#getLabelProvier()
	 */
	@Override
	public TAPAsGraphElementLabelProvider getLabelProvier() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsElementViewProvider#getInput(java.lang.String)
	 */
	@Override
	public Object getInput(String elementName) {
		return model;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsElementViewProvider#getElements()
	 */
	@Override
	public String[] getElements() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.cmg.tapas.extensions.TAPAsElementViewProvider#setModel(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void setModel(EObject model) {
		if (model instanceof Model) {
			this.model = (Model) model;
		} else {
			this.model = null;
		}
	}

}
