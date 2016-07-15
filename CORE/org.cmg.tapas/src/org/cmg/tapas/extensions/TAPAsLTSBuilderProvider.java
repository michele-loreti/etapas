/**
 * 
 */
package org.cmg.tapas.extensions;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public interface TAPAsLTSBuilderProvider {

	public TAPAsGraphElementContentProvider getContentProvider();
	
	public TAPAsGraphElementLabelProvider getLabelProvier();
	
	public String[] getProcesses();

	public void setModel( EObject model, IFile file );

	public LTSGraph<?, ?> generateLTS(String process);

	public LTSGraph<?, ?> minimizeLTS(LTSGraph<?, ?> graph);
}
