package org.cmg.tapas.clts.extensions

import org.cmg.tapas.views.TAPAsGraphElementContentProvider
import org.eclipse.jface.viewers.Viewer
import org.cmg.tapas.xtext.clts.composedLts.LtsDeclarationBody

class CLTSGraphElementContentProvider implements TAPAsGraphElementContentProvider{
	
	private LtsDeclarationBody ltsBody;
	
	override getRelationships(Object source, Object dest){
		this.ltsBody.rules.filter[r | r.src == source ].filter[trg == dest].toList.toArray
	}
	
	override getElements(Object inputElement) {
		if (inputElement instanceof LtsDeclarationBody) {
			ltsBody = inputElement as LtsDeclarationBody
		}
				(ltsBody?.states?.toList?.toArray) ?: newArrayOfSize(0)
	}
	
	override dispose() {
		
	}
	
	override inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	
	}
	
}