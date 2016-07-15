/**
 * 
 */
package org.cmg.tapas.core.graph.visitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.NoEdgeFilter;
import org.cmg.tapas.core.graph.StateInterface;


/**
 * @author loreti
 *
 */
public class DotVisitor<P extends StateInterface,R extends ActionInterface> 
					extends VisitGraph<P,R> {
	
	protected Map<P,Integer> nodeMap;
	protected String name;
	protected int count = 0;
	private PrintStream out;

	public DotVisitor(PrintStream out, String name , GraphData<P, R> graph ) {
		super( graph );
		this.name = name;
		nodeMap = new HashMap<P, Integer>();
		this.out = out;
	}
	public DotVisitor(String name , GraphData<P, R> graph ) {
		super( graph );
		this.name = name;
		nodeMap = new HashMap<P, Integer>();
	}
	
	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doEnd()
	 */
	@Override
	protected void doEnd() {
		result += "\n}";
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doStart()
	 */
	@Override
	protected void doStart() {
		result = "digraph "+name+" {\n";
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doVisit(graph.StateInterface)
	 */
	@Override
	protected void doVisit(P current) {
	}

	protected int getIndex(P node) {
		Integer x = nodeMap.get(node);
		if (x == null) {
			x = count++;
			nodeMap.put(node, x);
		}
		return x;
	}
	
	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doVisitTransition(graph.StateInterface, graph.ActionInterface, graph.StateInterface)
	 */
	@Override
	protected void doVisitTransition(P src, R act, P dest) {
		result += getIndex(src)+" -> "+getIndex(dest)+ "[ label=\""+act.toString()+"\" ]\n";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (result == null) {
			visit(new NoEdgeFilter<P, R>());
		}
		return result;
	}

	public void toDotFormat() {
		out.println(result);
	}
	

}
