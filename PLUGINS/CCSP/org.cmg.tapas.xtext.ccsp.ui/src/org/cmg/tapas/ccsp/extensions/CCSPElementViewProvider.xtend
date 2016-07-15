package org.cmg.tapas.ccsp.extensions

import org.cmg.tapas.extensions.TAPAsElementViewProvider
import org.eclipse.emf.ecore.EObject
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model

class CCSPElementViewProvider implements TAPAsElementViewProvider {
	
	Model currentModel;
	
	override getContentProvider() {
		new CCSPGraphElementContentProvider()
	}
	
	override getElements() {
		return currentModel.processes.map[ p | p.name ].sort
	}
	
	override getInput(String elementName) {
		return currentModel.processes.findFirst[ p | p.name.equals(elementName) ]
	}
	
	override getLabelProvier() {
		return new CCSPGraphElementLabelProvider()
	}
	
	override setModel(EObject model) {
		if (model instanceof Model) {
			currentModel = model
		} else {
			currentModel = null
		}
	}
	
}