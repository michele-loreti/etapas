/**
 * 
 */
package org.cmg.tapas.ccsp.extensions;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.ccsp.runtime.CCSPAction;
import org.cmg.tapas.ccsp.runtime.CCSPProcess;
import org.cmg.tapas.pa.LTSGraph;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
//import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author loreti
 *
 */
public class TAPAsLTSContentProvider implements TAPAsGraphElementContentProvider {
	
	private LTSGraph<CCSPProcess, CCSPAction> graph;

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		try {
			this.graph = (LTSGraph<CCSPProcess, CCSPAction>) inputElement;
			return this.graph.getStates().toArray();
		} catch (ClassCastException e) {
		}
		return null;
	}

	@Override
	public Object[] getRelationships(Object source, Object dest) {
		if (this.graph == null) {
			return null;
		}
		if ((source instanceof CCSPProcess)&&(dest instanceof CCSPProcess)) {
			CCSPProcess pSource = (CCSPProcess) source;
			CCSPProcess pDest = (CCSPProcess) dest;
			Map<CCSPAction, ? extends Set<CCSPProcess>> postSet = this.graph.getPostsetMapping(pSource);
			if (postSet != null) {
				LinkedList<CCSPTransition> actions = new LinkedList<>();
				for (CCSPAction ccspAction : postSet.keySet()) {
					if (postSet.get(ccspAction).contains(pDest)) {
						actions.add(new CCSPTransition(pSource, ccspAction, pDest));
					}
				}
				if (actions.size()>0) {
					return actions.toArray();
				}
			}
		}
		return null;
	}

}
