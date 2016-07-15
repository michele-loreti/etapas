/**
 * 
 */
package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.formulae.ltl.LtlFormula;
import org.cmg.tapas.formulae.ltl.LtlModelChecker;
import org.cmg.tapas.pa.LTSGraph;

/**
 * @author loreti
 *
 */
public class CLTSUtil {

	public static boolean check( CltsProcess p , LtlFormula<CltsProcess> f) {
		LTSGraph<CltsProcess, CltsAction> lts = new LTSGraph<CltsProcess, CltsAction>();
		lts.addState( (CltsProcess) p );
		lts.expand();

		//Se esistono stati di deadlock aggiunge un self loop
		Graph<CltsProcess, CltsAction> graph = lts.getGraph();
		for(CltsProcess s : graph.getStates()){
			if(graph.getPostset(s).isEmpty()){
				graph.addEdge(s, new CltsAction(" "), s);
			}
		}
		
		Set<CltsProcess> initialStates = new HashSet<CltsProcess>();
		initialStates.add( (CltsProcess) p );
		LtlModelChecker<CltsProcess> mc = new LtlModelChecker<CltsProcess>(graph, initialStates);
		return mc.check(f);		
	}
	
}
