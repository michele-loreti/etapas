package org.cmg.tapas.views;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.extensions.TAPAsLTSBuilderProvider;
import org.cmg.tapas.extensions.TAPAsModelCheckerProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.wb.swt.SWTResourceManager;

public class TAPAsModelCheckingView extends ViewPart {

	public static final String ID = "org.cmg.tapas.views.TAPAsModelCheckingView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Tree formulaeTree;
	private TAPAsModelCheckerProvider provider;
	private ToolItem checkButton;
	private Tree systemTree;
	private Text logArea;
	private IPartListener2 pl;

	public TAPAsModelCheckingView() {
		installHandler();
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(2, false));
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		toolkit.adapt(toolBar);
		toolkit.paintBordersFor(toolBar);
		
		checkButton = new ToolItem(toolBar, SWT.NONE);
		checkButton.setText("Check");
		checkButton.setImage(SWTResourceManager.getImage(TAPAsModelCheckingView.class, "/org/cmg/tapas/views/icons/lrun_obj.gif"));
		
		ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
		tltmClear.setImage(SWTResourceManager.getImage(TAPAsModelCheckingView.class, "/org/cmg/tapas/views/icons/trash.gif"));
		tltmClear.setText("Clear");
		tltmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logArea.setText("");
			}
		});
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCheck();
			}
		});
		
		{
			Group composite = new Group(container, SWT.BORDER | SWT.SHADOW_ETCHED_OUT);
			composite.setText("Elements");
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			toolkit.adapt(composite);
			toolkit.paintBordersFor(composite);
			{
				systemTree = new Tree(composite, SWT.BORDER);
				systemTree.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						enableButtons();
					}
				});
				toolkit.adapt(systemTree);
				toolkit.paintBordersFor(systemTree);
			}
		}
		{
			Group composite = new Group(container, SWT.NONE);
			composite.setText("Formulae");
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			toolkit.adapt(composite);
			toolkit.paintBordersFor(composite);
			
			formulaeTree = new Tree(composite, SWT.BORDER | SWT.MULTI);
			formulaeTree.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					enableButtons();
				}
			});
			toolkit.adapt(formulaeTree);
			toolkit.paintBordersFor(formulaeTree);
		}
		
		logArea = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		logArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.adapt(logArea, true, true);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		page.removePartListener(pl);
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
	
	private void updateView( ) {
		if (provider != null) {
			if (provider.isCatecorized()) {
				fillTreeWithCategories( systemTree );
			} else {
				fillTree( systemTree );
			}
			fillFomulaTree( );
		}
		enableButtons();
	}
	
	private void fillFomulaTree() {
		if (provider.getFormulaeCategories().length != 0 || provider.getFormulaeCategories() != null){
			fillFormulaTreeWithCategories();
		}else{
			String[] elements = provider.getFormulae();
			if (elements.length>0) {
				for (String f : elements) {
					TreeItem trtmP = new TreeItem(formulaeTree, SWT.NONE);
					trtmP.setText(f);
				}
			}
		}
	}
	
	private void fillFormulaTreeWithCategories() {
		String[] categories = provider.getFormulaeCategories();
		for(String c : categories){
			String[] elements = provider.getFormulae(c);
			if(elements.length > 0){
				TreeItem categoryElement = new TreeItem(formulaeTree, SWT.NONE);
				categoryElement.setText(c);
				for (String f : elements) {
					TreeItem trtmP = new TreeItem(categoryElement, SWT.NONE);
					trtmP.setText(f);
				}
				categoryElement.setExpanded(false);
			}
		}
		
	}

	private void clearView() {
		systemTree.removeAll();
		formulaeTree.removeAll();
	}

	private void fillTree(Tree tree) {
		LabeledElement[] elements = provider.getProcesses();
		if (elements.length>0) {
			for (LabeledElement p : elements) {
				TreeItem trtmP = new TreeItem(tree, SWT.NONE);
				trtmP.setText(p.getName());
				trtmP.setData(p.getElement());
			}
		}
	}

	private void fillTreeWithCategories(Tree tree ) {
		String[] categories = provider.getCategories();
		for (String c : categories) {
			LabeledElement[] elements = provider.getProcesses(c);
			if (elements.length>0) {
				TreeItem categoryElement = new TreeItem(tree, SWT.NONE);
				categoryElement.setText(c);
				for (LabeledElement p : elements) {
					TreeItem trtmP = new TreeItem(categoryElement, SWT.NONE);
					trtmP.setText(p.getName());
					trtmP.setData(p.getElement());
				}
				categoryElement.setExpanded(false);
			}

		}
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
						   TAPAsModelCheckerProvider provider = TAPAsProjectHelper.getModelCheckerProvider(extension);
						   if (provider != null) {
							   //EObject model = TAPAsProjectHelper.getModel(editor);
							   provider.setModel( model , file );
						   }
						   setModelCheckerProvider( provider );
					   }
				   }
				   
				   /*
				    * Loreti
				    */
//			     if(partRef.getPart(true) instanceof XtextEditor){
//			      XtextEditor editor = (XtextEditor) partRef.getPart(false);
//			      System.out.println("ACTIVATED EC_VIEW: "+editor.getTitle());
//					IFile file = (IFile) editor.getResource();
//					String extension = file.getFileExtension();
//					TAPAsModelCheckerProvider provider = TAPAsProjectHelper.getModelCheckerProvider(extension);
//					if (provider != null) {
//						EObject model = TAPAsProjectHelper.getModel(editor);
//						provider.setModel( model , file );
//					}
//					setModelCheckerProvider( provider );
//			     }
			   }
			  };
			  page.addPartListener(pl);		
	}

	protected void setModelCheckerProvider(TAPAsModelCheckerProvider provider) {
		if (this.provider == provider) {
			return ;
		}
		this.provider = provider;
		clearView();
		updateView();
	}
	
	protected void enableButtons() {
		boolean flag = true;
		TreeItem[] element = systemTree.getSelection();
		if (element.length == 0) {
			checkButton.setEnabled(false);
			return ;
		}
		if (element[0].getItems().length != 0) {
			checkButton.setEnabled(false);
			return ;
		}
		element = formulaeTree.getSelection();
		if (element.length == 0) {
			checkButton.setEnabled(false);
			return ;
		}
		if (element[0].getItems().length != 0) {
			checkButton.setEnabled(false);
			return ;
		}
		checkButton.setEnabled(flag);
	}

	private void doCheck( ) {
		String formulaName = formulaeTree.getSelection()[0].getText();
		String systemName = systemTree.getSelection()[0].getText();
		logArea.append(
				"\n\nStart model checking..."+
				"\nElement: "+systemName+
				"\nFormula: "+formulaName);
		//FIXME: Qui bisogna mettere la partenza di un nuovo processo che scrive il risultato alla fine!
		if (provider.check(systemTree.getSelection()[0].getData(),formulaeTree.getSelection()[0].getText())) {
			logArea.append("\nRESULT: Formula "+formulaName+" is satisfied by "+systemName+"!");
		} else {
			logArea.append("\nRESULT: Formula "+formulaName+" is not satisfied by "+systemName+"!\n");
		}
	}

}
