/**
 * 
 */
package org.cmg.tapas.views;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.pa.LTSGraph;
//import org.eclipse.gef4.layout.LayoutAlgorithm;
//import org.eclipse.gef4.zest.core.viewers.GraphViewer;
//import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;

/**
 * @author loreti
 *
 */
public class LTSTabFolder extends CTabItem {

	private GraphViewer graphViewer;
	private LTSGraph<?,?> graph;

	public LTSTabFolder(CTabFolder parent, String name, LTSGraph<?, ?> graph , TAPAsGraphElementContentProvider contentProvider , TAPAsGraphElementLabelProvider labelProvider, LayoutAlgorithm layoutAlgorithm ) {
		super(parent, SWT.CLOSE);
		setText(name);
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.pack();
		graphViewer = new GraphViewer( composite , SWT.NONE );
		graphViewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		graphViewer.setContentProvider(contentProvider);
		graphViewer.setLabelProvider(labelProvider);
		graphViewer.setLayoutAlgorithm(layoutAlgorithm);
		graphViewer.setInput(graph);
		this.graph = graph;
	}
	
	public void redrawGraph() {
		graphViewer.refresh();

	}
	
	//TODO ERCOLI change
	public LTSGraph<?,?> getLTSGraph(){
		return graph;
	}

}
