package org.cmg.tapas.handlers;




import org.cmg.tapas.views.TAPAsElementView;
//import org.cmg.tapas.extensions.TAPAsViewBuilder;
//import org.cmg.tapas.views.ElementView;
//import org.cmg.tapas.views.ModelCheckingView;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ElementViewHandler extends AbstractToolHandler {
	
	public ElementViewHandler(){
		
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
//		LinkedList<TAPAsViewBuilder> contents = loadViewBuilders(extension);
//		if (contents.size() == 0) {
//			return null;
//		}
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		try {
			IViewPart view = page.findView(TAPAsElementView.ID);
			if(page.isPartVisible(view))
				page.hideView(view);
			view = page.showView(TAPAsElementView.ID);
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
//	private LinkedList<TAPAsViewBuilder> loadViewBuilders( String fileExtension ) {
//		LinkedList<TAPAsViewBuilder> toReturn = new LinkedList<TAPAsViewBuilder>();
//		for (IConfigurationElement e : elements) {
//			if (e.getAttribute("extension").equals(fileExtension)) {
//		        Object o;
//				try {
//					o = e.createExecutableExtension("class");
//		            if (o instanceof TAPAsViewBuilder) {
//		            	TAPAsViewBuilder tmp = (TAPAsViewBuilder) o;
//		            	toReturn.add(tmp);
//		            }
//				} catch (CoreException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			System.out.println("Ext: "+e.getAttribute("extension")+" Model-checker: "+e.getAttribute("class"));
//		}		
//		return toReturn;
//	}

}
