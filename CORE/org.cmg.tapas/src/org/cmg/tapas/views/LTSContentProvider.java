/**
 * 
 */
package org.cmg.tapas.views;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.LTSGraph;
//import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.cmg.tapas.pa.Process;

/**
 * @author loreti
 *
 */
public class LTSContentProvider<S extends Process<S,A>,A extends ActionInterface> implements TAPAsGraphElementContentProvider {
	
	private LTSGraph<S, A> graph;

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
			this.graph = (LTSGraph<S, A>) inputElement;
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
		if ((source instanceof Process<?,?>)&&(dest instanceof Process<?,?>)) {
			Process<?,?> pSource = (Process<?,?>) source;
			Process<?,?> pDest = (Process<?,?>) dest;
			Map<A, ? extends Set<S>> postSet = this.graph.getPostsetMapping(pSource);
			if (postSet != null) {
				LinkedList<TAPAsGraphElementRelation> actions = new LinkedList<TAPAsGraphElementRelation>();
				for (Object action : postSet.keySet()) {
					if (postSet.get(action).contains(pDest)) {
						actions.add(new TAPAsGraphElementRelation(pSource, action, pDest));
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
