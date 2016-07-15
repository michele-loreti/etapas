package org.cmg.tapas.ccsp.extensions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class CCSPEquivelenceCheckerComposite extends Composite {

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CCSPEquivelenceCheckerComposite(Composite parent, int style) {
		super(parent, style);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
			}
		});
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);
		setLayout(new GridLayout(2, false));
		
		Tree element1 = new Tree(this, SWT.BORDER);
		element1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.adapt(element1);
		toolkit.paintBordersFor(element1);
		
		TreeItem processesOfElement1 = new TreeItem(element1, SWT.NONE);
		processesOfElement1.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		processesOfElement1.setText("Processes:");
		
		TreeItem systemsOfElement1 = new TreeItem(element1, SWT.NONE);
		systemsOfElement1.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		systemsOfElement1.setText("Systems:");
		
		Tree element2 = new Tree(this, SWT.BORDER);
		element2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.adapt(element2);
		toolkit.paintBordersFor(element2);

		TreeItem processesOfElement2 = new TreeItem(element2, SWT.NONE);
		processesOfElement2.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		processesOfElement2.setText("Processes:");
		
		TreeItem systemsOfElement2 = new TreeItem(element2, SWT.NONE);
		systemsOfElement2.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		systemsOfElement2.setText("Systems:");
		
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		
		Label lblEquivalence = new Label(composite, SWT.NONE);
		lblEquivalence.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEquivalence.setBounds(0, 0, 59, 14);
		toolkit.adapt(lblEquivalence, true, true);
		lblEquivalence.setText("Equivalence:");
		
		Combo equivalencecb = new Combo(composite, SWT.NONE);
		equivalencecb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		equivalencecb.setItems( new String[] { "Strong Bisimulation" , "Weak Bisimulation" , "Branching Bisimulation" , "Trace equivalence" } );
		toolkit.adapt(equivalencecb);
		toolkit.paintBordersFor(equivalencecb);
		
		Button btnCheck = new Button(composite, SWT.NONE);
		toolkit.adapt(btnCheck, true, true);
		btnCheck.setText("Check");
		
		text = new Text(this, SWT.BORDER | SWT.MULTI);
		text.setEditable(false);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_text.heightHint = 108;
		text.setLayoutData(gd_text);
		toolkit.adapt(text, true, true);

	}
}
