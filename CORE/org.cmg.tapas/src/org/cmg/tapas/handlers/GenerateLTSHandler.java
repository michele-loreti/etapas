package org.cmg.tapas.handlers;

import java.util.LinkedList;


import org.cmg.tapas.views.TAPAsLTSView;
//import org.cmg.tapas.extensions.TAPAsLTSBuilder;
//import org.cmg.tapas.extensions.TAPAsToolCompositeBuilder;
//import org.cmg.tapas.views.LTSView;
//import org.cmg.tapas.views.ModelCheckingView;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class GenerateLTSHandler extends AbstractToolHandler {
	
	public GenerateLTSHandler(){
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
//		LinkedList<TAPAsLTSBuilder> contents = loadLTSBuilders(extension);
//		if (contents.size() == 0) {
//			return null;
//		}
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			IViewPart view = page.findView(TAPAsLTSView.ID);
			if(page.isPartVisible(view))
				page.hideView(view);
			view = page.showView(TAPAsLTSView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	private LinkedList<TAPAsLTSBuilder> loadLTSBuilders( String fileExtension ) {
//		LinkedList<TAPAsLTSBuilder> toReturn = new LinkedList<TAPAsLTSBuilder>();
//		for (IConfigurationElement e : lts_builders) {
//			if (e.getAttribute("extension").equals(fileExtension)) {
//		        Object o;
//				try {
//					o = e.createExecutableExtension("class");
//		            if (o instanceof TAPAsLTSBuilder) {
//		            	TAPAsLTSBuilder tmp = (TAPAsLTSBuilder) o;
//		            	toReturn.add(tmp);
//		            }
//				} catch (CoreException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			System.out.println("Ext: "+e.getAttribute("extension")+" Lts-Builder: "+e.getAttribute("class"));
//		}		
//		return toReturn;
//	}
}
