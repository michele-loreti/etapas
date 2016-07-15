package org.cmg.tapas.handlers;

import java.util.LinkedList;



import org.cmg.tapas.views.TAPAsEquivalenceCheckerView;
//import org.cmg.tapas.extensions.TAPAsToolCompositeBuilder;
//import org.cmg.tapas.views.EquivalenceCheckerView;
//import org.cmg.tapas.views.ModelCheckingView;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class EquivalenceCheckerHandler extends AbstractToolHandler {
	
	public EquivalenceCheckerHandler(){
	
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		XtextEditor editor = getXtextEditor(event);
//		if (editor == null) {
//			return null;
//		}
//		EObject model = getModel(editor);
//		if (model == null) {
//			return null;
//		}
//		IFile file = (IFile) editor.getResource();
//		String extension = file.getFileExtension();
//		LinkedList<TAPAsToolCompositeBuilder> languageEquivalenceCheckers = loadEquivalenceCheckers(extension);
//		if (languageEquivalenceCheckers.size() == 0) {
//			return null;
//		}
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			IViewPart view = page.findView(TAPAsEquivalenceCheckerView.ID);
			if(page.isPartVisible(view))
				page.hideView(view);
			view = page.showView(TAPAsEquivalenceCheckerView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
//	
//	private LinkedList<TAPAsToolCompositeBuilder> loadEquivalenceCheckers( String fileExtension ) {
//		LinkedList<TAPAsToolCompositeBuilder> toReturn = new LinkedList<TAPAsToolCompositeBuilder>();
//		for (IConfigurationElement e : elements) {
//			if (e.getAttribute("extension").equals(fileExtension)) {
//		        Object o;
//				try {
//					o = e.createExecutableExtension("class");
//		            if (o instanceof TAPAsToolCompositeBuilder) {
//		            	TAPAsToolCompositeBuilder tmp = (TAPAsToolCompositeBuilder) o;
//		            	toReturn.add(tmp);
//		            }
//				} catch (CoreException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			System.out.println("Ext: "+e.getAttribute("extension")+" Equivalence-checker: "+e.getAttribute("class"));
//		}		
//		return toReturn;
//	}

}
