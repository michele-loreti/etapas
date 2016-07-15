/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.pa.LTSGraph;

/**
 * @author loreti
 *
 */
public class CCSPUtil {

	public static LTSGraph<CCSPProcess, CCSPAction> getGraph(CCSPProcess process) {
		LTSGraph<CCSPProcess, CCSPAction> graph = new LTSGraph<CCSPProcess, CCSPAction>();
		graph.addState(process);
		graph.expand();
		return graph;		
	}

	public static boolean modelCheckingHML( CCSPProcess p , HmlFormula<CCSPProcess, CCSPAction> f ) {
		return f.satisfies(p);
	}
}
