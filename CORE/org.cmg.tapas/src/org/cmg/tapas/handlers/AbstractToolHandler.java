/*
 * Michele Loreti, Concurrency and Mobility Group
 * Universitˆ di Firenze, Italy
 * (C) Copyright 2013.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Michele Loreti
 */
package org.cmg.tapas.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * @author loreti
 *
 */
public abstract class AbstractToolHandler extends AbstractHandler {

	/**
	 * 
	 */
	public AbstractToolHandler() {
		super();
	}

	protected XtextEditor getXtextEditor(ExecutionEvent event) {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof XtextEditor) {
			return (XtextEditor) editor;
		}
		return null;
	}

	protected EObject getModel(XtextEditor editor) {
		if (editor instanceof XtextEditor) {
			XtextEditor xEditor = (XtextEditor) editor;
	
			EList<EObject> values = xEditor.getDocument().readOnly(new IUnitOfWork<EList<EObject>, XtextResource>(){
	
				@Override
				public EList<EObject> exec(XtextResource state) throws Exception {
					if (state.getErrors().size()>0) {
						return null;
					}
					return state.getContents();
				}
				
			});		
			if ((values != null)&&(values.size() > 0)) {
				return values.get(0);
			}
		}
		return null;
	}

}