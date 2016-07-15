package org.cmg.tapas.core.graph.visitor;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.EdgeFilter;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.StateInterface;


public abstract class VisitGraph<P extends StateInterface,R extends ActionInterface> {
	
	protected String result = null;
	protected GraphData<P, R> graph;
	
	public VisitGraph( GraphData<P, R> graph ) {
		this.graph = graph;
	}
		
	public VisitGraph() {
		this.graph = null;
	}
	
	public void setGraph( GraphData<P, R> graph ) {
		this.graph = graph;
	}
	
	/**
	 * 
	 * @param f
	 */
	public final void visit(EdgeFilter<P,R> f) {
		
		doStart();
		Set<P> states = graph.getStates();
		for (P current : states) {
			doVisit( current );
			Iterator<Entry<R, P>> i = graph.getImageIterator(current, true, f);
			Entry<R,P> foo;
			while (i.hasNext()) {
				foo = i.next();
				doVisitTransition( current , foo.getKey() , foo.getValue() );
			}
		}
		
		doEnd();
	}
	
	protected abstract void doVisit(P current);	
	protected abstract void doStart( );
	protected abstract void doEnd( );
	protected abstract void doVisitTransition(P src, R act, P dest);
	
	
}
