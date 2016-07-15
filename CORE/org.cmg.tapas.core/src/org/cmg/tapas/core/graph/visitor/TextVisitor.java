/**
 * 
 */
package org.cmg.tapas.core.graph.visitor;

import java.io.PrintStream;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.GraphData;
import org.cmg.tapas.core.graph.StateInterface;


/**
 * @author loreti
 *
 */
public class TextVisitor<
		P extends StateInterface,
		R extends ActionInterface
	> extends VisitGraph<P ,R> {
	
	private PrintStream out;

	public TextVisitor(PrintStream out , GraphData<P, R> graph ) {
		super( graph );
		this.out = out;
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doEnd()
	 */
	@Override
	protected void doEnd() {
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doStart()
	 */
	@Override
	protected void doStart() {
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doVisit(graph.StateInterface)
	 */
	@Override
	protected void doVisit(P current) {
	}

	/* (non-Javadoc)
	 * @see vGraph.VisitGraph#doVisitTransition(graph.StateInterface, graph.ActionInterface, graph.StateInterface)
	 */
	@Override
	protected void doVisitTransition(P src, R act,
			P dest) {
		out.println(src+" - "+act+" -> "+dest);
	}
}
