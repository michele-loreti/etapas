//package org.cmg.tapas.ccsp.extensions;
//
//import java.util.HashMap;
//
//import org.cmg.tapas.extensions.TAPAsViewBuilder;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NextProcess;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ReferenceInProcess;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration;
//import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Transition;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.emf.common.util.EList;
//import org.eclipse.emf.ecore.EObject;
////import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
////import org.eclipse.gef4.zest.core.viewers.GraphViewer;
////import org.eclipse.gef4.zest.core.widgets.Graph;
////import org.eclipse.gef4.zest.core.widgets.GraphConnection;
////import org.eclipse.gef4.zest.core.widgets.GraphNode;
////import org.eclipse.gef4.zest.core.widgets.ZestStyles;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CTabFolder;
//import org.eclipse.swt.custom.CTabItem;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.TabFolder;
//import org.eclipse.swt.widgets.TabItem;
//
//
//public class CCSPViewBuilder implements TAPAsViewBuilder {
//	
//	private TabFolder folder;
//	
//	public CCSPViewBuilder() {
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public Composite createComposite(Composite context, EObject o, IFile file) {
//		if(!(o instanceof Model)){
//			return null;
//		}
//		Model model = (Model) o;
////		Display display = context.getDisplay();
////		Shell shell = new Shell(display);
////		shell.setSize(400, 400);
////		shell.setLayout(new FillLayout());
//		folder = new TabFolder(context, SWT.NONE);
//		folder.setLayout(new FillLayout(SWT.HORIZONTAL));
//		EList<ProcessDeclaration> processes = model.getProcesses();
//		for(ProcessDeclaration p : processes ){
//			TabItem item = new TabItem(folder, SWT.NONE) ;
//			item.setText(p.getName());
//			Composite cItem = new Composite (folder, SWT.NONE);
//			FillLayout layout = new FillLayout(SWT.HORIZONTAL);
//			cItem.setLayout(layout);
//			setItemContent(p, cItem);
//			item.setControl(cItem);
//			cItem.pack();
//		}
//		folder.pack();
//	
////		shell.open();
////		while (!shell.isDisposed ()) {
////			if (!display.readAndDispatch ()) display.sleep ();
////		}
////		display.dispose ();
//		return folder;
//	}
//	
//	private GraphViewer setItemContent(ProcessDeclaration process, Composite parent){
//		GraphViewer viewer = new GraphViewer(parent, SWT.BORDER);
//		viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
//		viewer.setContentProvider(new ZestRelationshipContentProvider());
//		viewer.setLabelProvider(new ZestLabelProvider());
//		CCSPDataModel dataModel = new CCSPDataModel(process);
//		viewer.setInput(dataModel.getNodes());
//		viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN) );
//		viewer.applyLayout();
//		return viewer;
//	}
//	
//}
