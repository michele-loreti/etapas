package org.cmg.tapas.clts.extensions

import org.cmg.tapas.extensions.TAPAsElementViewProvider
import org.eclipse.emf.ecore.EObject
import org.cmg.tapas.xtext.clts.composedLts.Model
import org.cmg.tapas.xtext.clts.composedLts.Lts
import java.util.LinkedList
import org.cmg.tapas.xtext.clts.composedLts.LtsDeclarationBody
import org.cmg.tapas.xtext.clts.composedLts.LtsState

class CLTSElementViewProvider implements TAPAsElementViewProvider {
	
	private Model model;
	
	override getContentProvider() {
		return new CLTSGraphElementContentProvider()
	}
	
	override getElements() {
		var toReturn = new LinkedList<String>();
//		for(Lts l : model.elements.filter(typeof(Lts))){
//			if(l.body instanceof LtsDeclarationBody)
//				toReturn.add(l.name);
//		}
		for(Lts s : model.elements.filter(typeof(Lts))){
			
			toReturn.add(s.name);
		}
		print(toReturn)
		return toReturn
	}
	
	override getInput(String elementName) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override getLabelProvier() {
		return new CLTSGraphElementLabelProvider()
	}
	
	override setModel(EObject model) {
		if(model instanceof Model){
			this.model = model
		}else{
			this.model = null
		}
	}
	
}