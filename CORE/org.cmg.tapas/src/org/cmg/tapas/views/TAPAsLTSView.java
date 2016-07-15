package org.cmg.tapas.views;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.pa.LTSGraph;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
//import org.eclipse.gef4.layout.LayoutAlgorithm;
//import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
//import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
//import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
//import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TAPAsLTSView extends ViewPart {

	public static final String ID = "org.cmg.tapas.views.TAPAsLTSView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	
	private static String[] layouts =  new String[] { "Tree (Vertical)" , "Tree (Horizontal)" , "Spring" , "Radial" , "Grid" };
	private static LayoutAlgorithm[] layoutAlgorithms = new LayoutAlgorithm[] {
		new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING/*TreeLayoutAlgorithm.TOP_DOWN*/) , 
		new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING/*TreeLayoutAlgorithm.LEFT_RIGHT*/) , 
		new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING) , 
		new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING) , 
		new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING)		
	};
	
	private TAPAsLTSBuilderProvider provider;
	private Combo availableLayouts;
	private Combo selectedElement;
	private Button btnRefresh;
	private CTabFolder graphComposite;
	private Text text;
	//private GraphViewer graphViewer;
	private IPartListener2 pl;
	
	public TAPAsLTSView() {
		installHandler();
	}
	
	private void installHandler() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		pl = new IPartListener2() {			   
			   
			   @Override
			   public void partVisible(IWorkbenchPartReference partRef) {
				   System.out.println("VISIBLE LTS_VIEW: "+partRef.getTitle());
			   }
			   
			   @Override
			   public void partOpened(IWorkbenchPartReference partRef) {
			    // TODO Auto-generated method stub
			    
			   }
			   
			   @Override
			   public void partInputChanged(IWorkbenchPartReference partRef) {

			   }
			   
			   @Override
			   public void partHidden(IWorkbenchPartReference partRef) {
			    // TODO Auto-generated method stub
			    
			   }
			   
			   @Override
			   public void partDeactivated(IWorkbenchPartReference partRef) {
				   System.out.println("DEACTIVATED LTS_VIEW: "+partRef.getTitle());
			   }
			   
			   @Override
			   public void partClosed(IWorkbenchPartReference partRef) {
			    // TODO Auto-generated method stub
			    
			   }
			   
			   @Override
			   public void partBroughtToTop(IWorkbenchPartReference partRef) {
			    // TODO Auto-generated method stub
			    
			   }
			   
			   @Override
			   public void partActivated(IWorkbenchPartReference partRef) {
				   /*
				    * Modifica Ercoli
				    * 
				    * 
				    */
				   IEditorPart editor1 = page.getActiveEditor();
				   if(editor1 instanceof XtextEditor){
					   XtextEditor editor = (XtextEditor) editor1;
					   EObject model = TAPAsProjectHelper.getModel(editor);
					   if(model != null){
						   //System.out.println("ACTIVATED EC_VIEW: "+editor.getTitle());
						   IFile file = (IFile) editor.getResource();
						   String extension = file.getFileExtension();
						   TAPAsLTSBuilderProvider provider = TAPAsProjectHelper.getLTSBuilderProvider(extension);
						   if (provider != null) {
							   //EObject model = TAPAsProjectHelper.getModel(editor);
							   provider.setModel( model , file );
						   }
						   setLTSBuilderProvider(provider);
					   }
				   }
				   
				   /*
				    * Loreti
				    */
//			     if(partRef.getPart(true) instanceof XtextEditor){
//			      XtextEditor editor = (XtextEditor) partRef.getPart(false);
//			      System.out.println("ACTIVATED LTS_VIEW: "+editor.getTitle());
//					IFile file = (IFile) editor.getResource();
//					String extension = file.getFileExtension();
//					TAPAsLTSBuilderProvider provider = TAPAsProjectHelper.getLTSBuilderProvider(extension);
//					if (provider != null) {
//						EObject model = TAPAsProjectHelper.getModel(editor);
//						provider.setModel( model , file );
//					}
//					setLTSBuilderProvider(provider);
//			     }
			   }
			  };
			  page.addPartListener(pl);		
	}

	public void setLTSBuilderProvider( TAPAsLTSBuilderProvider provider ) {
		this.provider = provider;
		updatedView();
	}

	private void updatedView() {
		boolean areEnabled = false;
		String[] elements = new String[] {};
		if (this.provider != null) {
			elements = this.provider.getProcesses();
			if (elements.length>0) {
				areEnabled = true;
				selectedElement.setItems(elements);
			}
		}
		selectedElement.setEnabled(areEnabled);
		availableLayouts.setEnabled(areEnabled);
		btnRefresh.setEnabled(areEnabled);
	}

	private void applyGraphLayout() {
//		graphViewer.setLayoutAlgorithm(getSelectedLayoutAlgorithm());
//		graphViewer.refresh();
	}

	private LayoutAlgorithm getSelectedLayoutAlgorithm() {
		int selected = availableLayouts.getSelectionIndex();
		if (selected < 0) {
			selected = 0;
			availableLayouts.select(0);
		}
		return layoutAlgorithms[selected];
	}
	
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(1, false));
		{
			ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
			toolkit.adapt(toolBar);
			toolkit.paintBordersFor(toolBar);
			{
				ToolItem tltmExport = new ToolItem(toolBar, SWT.NONE);
				tltmExport.setImage(SWTResourceManager.getImage(TAPAsLTSView.class, "/org/cmg/tapas/views/icons/export_wiz.gif"));
				tltmExport.setText("Export");
			}
			{
				ToolItem tltmImport = new ToolItem(toolBar, SWT.NONE);
				tltmImport.setImage(SWTResourceManager.getImage(TAPAsLTSView.class, "/org/cmg/tapas/views/icons/import_wiz.gif"));
				tltmImport.setText("Import");
			}
			{
				ToolItem tltmMinimize = new ToolItem(toolBar, SWT.NONE);
				tltmMinimize.setImage(SWTResourceManager.getImage(TAPAsLTSView.class, "/org/cmg/tapas/views/icons/collapseall.gif"));
				tltmMinimize.setText("Minimize");
				tltmMinimize.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						addMinimizedLTSTabFolder();
						
					}
				});
			}
		}
		{
			Composite composite = new Composite(container, SWT.NONE);
			composite.setLayout(new GridLayout(5, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(composite);
			toolkit.paintBordersFor(composite);
			
			Label lblLayout = new Label(composite, SWT.NONE);
			lblLayout.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			toolkit.adapt(lblLayout, true, true);
			lblLayout.setText("Layout:");
			
			availableLayouts = new Combo(composite, SWT.NONE);
			availableLayouts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			availableLayouts.setItems(layouts);
			availableLayouts.select(0);
//			availableLayouts.addSelectionListener(new SelectionAdapter() {
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					
//					int idx = selectedElement.getSelectionIndex();
//					if (idx < 0) {
//						updateGraph( null );
//					} else {
//						updateGraph(selectedElement.getItem(idx));
//					}
//					
//				}
//			});
			toolkit.adapt(availableLayouts);
			toolkit.paintBordersFor(availableLayouts);
			
			
			Label lblElement = new Label(composite, SWT.NONE);
			lblElement.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			toolkit.adapt(lblElement, true, true);
			lblElement.setText("Process:");
			{
				selectedElement = new Combo(composite, SWT.READ_ONLY);
//				selectedElement.addSelectionListener(new SelectionAdapter() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//
//						int idx = selectedElement.getSelectionIndex();
//						if (idx < 0) {
//							updateGraph( null );
//						} else {
//							updateGraph(selectedElement.getItem(idx));
//						}
//						
//					}
//				});
				selectedElement.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				toolkit.adapt(selectedElement);
				toolkit.paintBordersFor(selectedElement);
			}
			
			btnRefresh = new Button(composite, SWT.NONE);
			btnRefresh.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					addLTSTabFolder();
					
				}
			});
			toolkit.adapt(btnRefresh, true, true);
			btnRefresh.setText("View");
		}
		{
			graphComposite = new CTabFolder(container, SWT.NONE);
			graphComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
			graphComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
			graphComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			toolkit.adapt(graphComposite);
			toolkit.paintBordersFor(graphComposite);
//			{
//				CTabItem tbtmNewItem = new CTabItem(graphComposite, SWT.CLOSE);
//				tbtmNewItem.setText("New Item");
//				{
//					Composite composite = new Composite(graphComposite, SWT.NONE);
//					tbtmNewItem.setControl(composite);
//					toolkit.paintBordersFor(composite);
//					composite.setLayout(new FillLayout(SWT.HORIZONTAL));
//					
//					text = new Text(composite, SWT.BORDER);
//					toolkit.adapt(text, true, true);
//				}
//			}
//			graphViewer = new GraphViewer( graphComposite , SWT.NONE );
//			graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		}

		
		createActions();
		initializeToolBar();
		initializeMenu();
		updatedView();
	}
	
	private void addMinimizedLTSTabFolder() {
		CTabItem selected = graphComposite.getSelection();
		if( selected instanceof LTSTabFolder){
			LTSTabFolder ltsTabSelected = (LTSTabFolder) selected;
			LTSGraph<?, ?> graph = ltsTabSelected.getLTSGraph();
			LTSGraph<?, ?> miniGraph = provider.minimizeLTS(graph);
			System.out.println("GRAPH MINIMIZED STATES: "+miniGraph.getNumberOfStates());
			LTSTabFolder created = new LTSTabFolder(graphComposite, ltsTabSelected.getText(), miniGraph , provider.getContentProvider(), provider.getLabelProvier(),getSelectedLayoutAlgorithm());
			graphComposite.setSelection(created);
			created.redrawGraph();
		}
		
	}
	
	protected void addLTSTabFolder() {
		int selectedIdx = selectedElement.getSelectionIndex();
		if ( selectedIdx >= 0 ) {
			String process = selectedElement.getItem(selectedIdx);
			LTSGraph<?, ?> graph = provider.generateLTS( process );
			System.out.println("LTS NODES: "+graph.getNumberOfStates());
			LTSTabFolder created = new LTSTabFolder(graphComposite, process, graph , provider.getContentProvider(), provider.getLabelProvier(),getSelectedLayoutAlgorithm());
			graphComposite.setSelection(created);
			created.redrawGraph();
		}
	}

	public void dispose() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		page.removePartListener(pl);
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
