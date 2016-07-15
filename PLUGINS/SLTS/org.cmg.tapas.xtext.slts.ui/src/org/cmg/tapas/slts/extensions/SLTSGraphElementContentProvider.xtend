package org.cmg.tapas.slts.extensions

import org.cmg.tapas.views.TAPAsGraphElementContentProvider
import org.eclipse.jface.viewers.Viewer
import org.cmg.tapas.xtext.slts.simpleLts.Model
import org.cmg.tapas.xtext.slts.simpleLts.State

class SLTSGraphElementContentProvider implements TAPAsGraphElementContentProvider { 
	
	Model model;
	
	
	override getElements(Object inputElement) {
		if (inputElement instanceof Model) {
			model = inputElement as Model
		}
		(model?.elements?.filter( typeof(State) )?.toList?.toArray) ?: newArrayOfSize(0)
	}
	
	override dispose() {

	}
	
	override inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
	
	override getRelationships(Object source, Object dest) {
		this.model.elements.filter( typeof(State) ).findFirst[ s | s == source ].rules.filter[
			r | r.next==dest 
		].toList.toArray
	}
	
	
	
}