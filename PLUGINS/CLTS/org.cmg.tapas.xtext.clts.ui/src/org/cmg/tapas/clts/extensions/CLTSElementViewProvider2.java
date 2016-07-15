package org.cmg.tapas.clts.extensions;

import java.util.LinkedList;

import org.cmg.tapas.extensions.TAPAsElementViewProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.clts.composedLts.Lts;
import org.cmg.tapas.xtext.clts.composedLts.LtsDeclarationBody;
import org.cmg.tapas.xtext.clts.composedLts.Model;
import org.eclipse.emf.ecore.EObject;

public class CLTSElementViewProvider2 implements TAPAsElementViewProvider {
	
	private Model model;
	
	@Override
	public TAPAsGraphElementContentProvider getContentProvider() {
		return new CLTSGraphElementContentProvider();
	}

	@Override
	public TAPAsGraphElementLabelProvider getLabelProvier() {
		return new CLTSGraphElementLabelProvider();
	}

	@Override
	public Object getInput(String elementName) {
		LtsDeclarationBody ltsBody;
		for(EObject o : model.eContents()){
			if(o instanceof Lts){
				if(((Lts) o).getName().equals(elementName)){
					ltsBody = (LtsDeclarationBody)((Lts)o).getBody();
					return ltsBody;
				}
			}
		}
		
		return null;
	}

	@Override
	public String[] getElements() {
		LinkedList<String> ltsNames = new LinkedList<String>();
		if (this.model == null) {
			return new String[] {};
		} else {
			for(EObject o : model.getElements()){
				if(o instanceof Lts){
					if(((Lts)o).getBody() instanceof LtsDeclarationBody){
						ltsNames.add(((Lts)o).getName());
					}
				}
			}
			return ltsNames.toArray(new String[ltsNames.size()]);
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
