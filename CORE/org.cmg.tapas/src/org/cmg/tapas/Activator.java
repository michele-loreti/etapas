package org.cmg.tapas;

import org.cmg.tapas.extensions.TAPAsElementViewProvider;
import org.cmg.tapas.views.TAPAsElementView;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cmg.tapas"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		addListeners();
	}

	private void addListeners() {
//		IWorkbench workbench = PlatformUI.getWorkbench();
//		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//		final IWorkbenchPage page = window.getActivePage();
//		 IPartListener2 pl = new IPartListener2() {			   
//			   
//			   @Override
//			   public void partVisible(IWorkbenchPartReference partRef) {
//				   System.out.println("VISIBLE: "+partRef.getTitle());
//			   }
//			   
//			   @Override
//			   public void partOpened(IWorkbenchPartReference partRef) {
//			    // TODO Auto-generated method stub
//			    
//			   }
//			   
//			   @Override
//			   public void partInputChanged(IWorkbenchPartReference partRef) {
//
//			   }
//			   
//			   @Override
//			   public void partHidden(IWorkbenchPartReference partRef) {
//			    // TODO Auto-generated method stub
//			    
//			   }
//			   
//			   @Override
//			   public void partDeactivated(IWorkbenchPartReference partRef) {
//				   System.out.println("DEACTIVATED: "+partRef.getTitle());
//			   }
//			   
//			   @Override
//			   public void partClosed(IWorkbenchPartReference partRef) {
//			    // TODO Auto-generated method stub
//			    
//			   }
//			   
//			   @Override
//			   public void partBroughtToTop(IWorkbenchPartReference partRef) {
//			    // TODO Auto-generated method stub
//			    
//			   }
//			   
//			   @Override
//			   public void partActivated(IWorkbenchPartReference partRef) {
//			    try {
//			     if(partRef.getPart(false) instanceof XtextEditor){
//			      XtextEditor editor = (XtextEditor) partRef.getPart(false);
//			      System.out.println("ACTIVATED: "+editor.getTitle());
//					IFile file = (IFile) editor.getResource();
//					String extension = file.getFileExtension();
//					TAPAsElementViewProvider provider = TAPAsProjectHelper.getElementViewProvider(extension);
//					if (provider != null) {
//						EObject model = TAPAsProjectHelper.getModel(editor);
//						provider.setModel( model );
//					}
//			      IViewPart view = page.showView(TAPAsElementView.ID);
//			      if (view instanceof TAPAsElementView) {
//			    	  ((TAPAsElementView) view).setElementViewProvider(provider);
//			      }
//			     }
//			    } catch (PartInitException e) {
//			     // TODO Auto-generated catch block
//			     e.printStackTrace();
//			    }
//			    
//			   }
//			  };
//			  page.addPartListener(pl);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	
}
