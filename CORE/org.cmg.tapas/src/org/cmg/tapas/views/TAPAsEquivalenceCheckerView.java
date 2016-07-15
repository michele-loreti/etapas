package org.cmg.tapas.views;

import org.cmg.tapas.TAPAsProjectHelper;
import org.cmg.tapas.extensions.TAPAsEquivalenceCheckerProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class TAPAsEquivalenceCheckerView extends ViewPart {

	public static final String ID = "org.cmg.tapas.views.TAPAsEquivalenceCheckerView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Tree leftTreeSelection;
	private Tree rightTreeSelection;
	private Text logArea;
	private TAPAsEquivalenceCheckerProvider provider;
	private Combo equivalenceSelection;
	private Combo algorithmSelection;
	private String[] equivalences;
	private String[] algorithms;
	private ToolItem checkButton;
	private ToolItem clearButton;
	private IPartListener2 pl;

	public TAPAsEquivalenceCheckerView() {
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
		container.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolkit.adapt(toolBar);
		toolkit.paintBordersFor(toolBar);
		
		checkButton = new ToolItem(toolBar, SWT.NONE);
		checkButton.setImage(SWTResourceManager.getImage(TAPAsEquivalenceCheckerView.class, "/org/cmg/tapas/views/icons/lrun_obj.gif"));
		checkButton.setText("Check");
		
		clearButton = new ToolItem(toolBar, SWT.NONE);
		clearButton.setImage(SWTResourceManager.getImage(TAPAsEquivalenceCheckerView.class, "/org/cmg/tapas/views/icons/trash.gif"));
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
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

		
		Composite processSelectionPanel = new Composite(container, SWT.NONE);
		processSelectionPanel.setLayout(new GridLayout(2, false));
		processSelectionPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.adapt(processSelectionPanel);
		toolkit.paintBordersFor(processSelectionPanel);
		
		leftTreeSelection = new Tree(processSelectionPanel, SWT.BORDER);
		leftTreeSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		leftTreeSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableCheckButton();
			}
		});
		
		toolkit.adapt(leftTreeSelection);
		toolkit.paintBordersFor(leftTreeSelection);
				
		rightTreeSelection = new Tree(processSelectionPanel, SWT.BORDER);
		rightTreeSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		rightTreeSelection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableCheckButton();
			}
		});
		
		toolkit.adapt(rightTreeSelection);
		toolkit.paintBordersFor(rightTreeSelection);
		{
			Composite toolPanel = new Composite(container, SWT.NONE);
			toolPanel.setLayout(new GridLayout(4, false));
			toolPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(toolPanel);
			toolkit.paintBordersFor(toolPanel);
			
			Label lblEquivalenceChecker = new Label(toolPanel, SWT.NONE);
			lblEquivalenceChecker.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			toolkit.adapt(lblEquivalenceChecker, true, true);
			lblEquivalenceChecker.setText("Equivalence:");
			
			equivalenceSelection = new Combo(toolPanel, SWT.NONE);
			equivalenceSelection.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int idx = equivalenceSelection.getSelectionIndex();
					if (idx < 0) {
						algorithmSelection.setEnabled(false);
						algorithmSelection.removeAll();
					} else {
						String[] algorithms = provider.getAlgorithms(idx);
						if ((algorithms == null) || (algorithms.length==0)) {
							algorithmSelection.setEnabled(false);
							TAPAsEquivalenceCheckerView.this.algorithms = new String[] {};
						} else {
							TAPAsEquivalenceCheckerView.this.algorithms = algorithms;
							algorithmSelection.setEnabled(true);
							algorithmSelection.setItems(algorithms);
						}
					}
					enableCheckButton();
				}
			});
			
			equivalenceSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			toolkit.adapt(equivalenceSelection);
			toolkit.paintBordersFor(equivalenceSelection);
			
			Label lblAlgorithm = new Label(toolPanel, SWT.NONE);
			lblAlgorithm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			toolkit.adapt(lblAlgorithm, true, true);
			lblAlgorithm.setText("Algorithm:");
			
			algorithmSelection = new Combo(toolPanel, SWT.NONE);
			algorithmSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			algorithmSelection.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					enableCheckButton();
				}
			});
			
			toolkit.adapt(algorithmSelection);
			toolkit.paintBordersFor(algorithmSelection);
		}
		
		Composite logPanel = new Composite(container, SWT.NONE);
		logPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		logPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		toolkit.adapt(logPanel);
		toolkit.paintBordersFor(logPanel);
		
		logArea = new Text(logPanel, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		toolkit.adapt(logArea, true, true);

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	protected void enableCheckButton() {
		TreeItem[] element = leftTreeSelection.getSelection();
		if (element.length == 0) {
			checkButton.setEnabled(false);
			return ;
		}
		if (element[0].getItems().length != 0) {
			checkButton.setEnabled(false);
			return ;
		}
		element = rightTreeSelection.getSelection();
		if (element.length == 0) {
			checkButton.setEnabled(false);
			return ;
		}
		if (element[0].getItems().length != 0) {
			checkButton.setEnabled(false);
			return ;
		}
		if ((!equivalenceSelection.isEnabled())||equivalenceSelection.getSelectionIndex()<0) {
			checkButton.setEnabled(false);
			return ;
		}
 		if ((algorithmSelection.isEnabled())&&(algorithmSelection.getSelectionIndex()<0)) {
			checkButton.setEnabled(false);
			return ;
 		}		
		checkButton.setEnabled(true);
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
				   System.out.println("OPENED LTS_VIEW: "+partRef.getTitle());
			    
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
				   System.out.println("ACTIVATED LTS_VIEW: "+partRef.getTitle());
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
						   TAPAsEquivalenceCheckerProvider provider = TAPAsProjectHelper.getEquivalenceCheckerProvider(extension);
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
//				   if(partRef.getPart(true) instanceof XtextEditor){
//					   XtextEditor editor = (XtextEditor) partRef.getPart(false);
//					   System.out.println("ACTIVATED EC_VIEW: "+editor.getTitle());
//					   IFile file = (IFile) editor.getResource();
//					   String extension = file.getFileExtension();
//					   TAPAsEquivalenceCheckerProvider provider = TAPAsProjectHelper.getEquivalenceCheckerProvider(extension);
//					   if (provider != null) {
//						   EObject model = TAPAsProjectHelper.getModel(editor);
//						   provider.setModel( model , file );
//					   }
//					   setLTSBuilderProvider(provider);
//				   }
			   }
			  };
			  page.addPartListener(pl);		
	}

	protected void setLTSBuilderProvider(
			TAPAsEquivalenceCheckerProvider provider) {
		if (this.provider == provider) {
			return ;
		}
		this.provider = provider;
		clearView();
		updateView();
	}

	private void clearView() {
		leftTreeSelection.removeAll();
		rightTreeSelection.removeAll();
	}

	private void updateView() {
		if (provider == null) {
			algorithmSelection.setEnabled(false);
			equivalenceSelection.setEnabled(false);
			checkButton.setEnabled(false);
			return ;
		}		
		if (provider.isCatecorized()) {
			fillTreesWithCatecories( leftTreeSelection );
			fillTreesWithCatecories( rightTreeSelection );
		} else {
			fillTrees( leftTreeSelection );
			fillTrees( rightTreeSelection );
		}
		this.equivalences = provider.getEquivalences();
		equivalenceSelection.setItems( this.equivalences );
	}

	private void fillTrees(Tree tree) {
		LabeledElement[] elements = provider.getProcesses();
		if (elements.length>0) {
			for (LabeledElement p : elements) {
				TreeItem trtmP = new TreeItem(tree, SWT.NONE);
				trtmP.setText(p.getName());
				trtmP.setData(p.getElement());
			}
		}
	}

	private void fillTreesWithCatecories(Tree tree ) {
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
	
	
	private void doCheck( ) {
		boolean withAlgorithm = algorithms.length>0;
		logArea.append(
				"\nChecking: "+equivalences[equivalenceSelection.getSelectionIndex()]+
				(withAlgorithm?"\nwith algorithm: "+algorithms[algorithmSelection.getSelectionIndex()]:"")+
				"\nArgument 1: "+leftTreeSelection.getSelection()[0].getText()+
				"\nArgument 2: "+rightTreeSelection.getSelection()[0].getText());
		//FIXME: Qui bisogna mettere la partenza di un nuovo processo che scrive il risultato alla fine!
		if (withAlgorithm && 
				provider.check(
						equivalenceSelection.getSelectionIndex(), 
						algorithmSelection.getSelectionIndex(), 
						leftTreeSelection.getSelection()[0].getData(), 
						rightTreeSelection.getSelection()[0].getData())) {
			logArea.append("\nRESULT: The two systems are equivalent!");
			return ;
		}
		if (!withAlgorithm && 
				provider.check(
						equivalenceSelection.getSelectionIndex(), 
						leftTreeSelection.getSelection()[0].getData(), 
						rightTreeSelection.getSelection()[0].getData())) {
			logArea.append("\nRESULT: The two systems are equivalent!");
			return ;
		}
		logArea.append("\nRESULT: The two systems are not equivalent!");
	}
		
}
