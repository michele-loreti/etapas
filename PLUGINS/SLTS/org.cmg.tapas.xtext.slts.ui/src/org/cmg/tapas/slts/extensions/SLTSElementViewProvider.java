/**
 * 
 */
package org.cmg.tapas.slts.extensions;

import org.cmg.tapas.extensions.TAPAsElementViewProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.slts.simpleLts.Model;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public class SLTSElementViewProvider implements TAPAsElementViewProvider {

	private Model model;

	/**
	 * 
	 */
	public SLTSElementViewProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		return new SLTSGraphElementContentProvider( );
	}

	@Override
	public TAPAsGraphElementLabelProvider getLabelProvier() {
		return new SLTSGraphElementLabelProvider();
	}

	@Override
	public Object getInput(String elementName) {
		return model;
	}

	@Override
	public String[] getElements() {
		if (this.model == null) {
			return new String[] {};
		} else {
			return new String[] { "Transition System: "+this.model.getName() };
		}
	}

	@Override
	public void setModel(EObject model) {
		if (model instanceof Model) {
			this.model = (Model) model;
		} else {
			this.model = null;
		}
	}

}
