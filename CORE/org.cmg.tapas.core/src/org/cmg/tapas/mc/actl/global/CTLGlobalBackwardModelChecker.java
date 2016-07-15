/**
 * 
 */
package org.cmg.tapas.mc.actl.global;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.StateInterface;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.And;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.AtomicPredicate;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.ExistsAlways;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.ExistsNext;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.ExistsUntil;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.Not;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula.True;
import org.cmg.tapas.mc.actl.global.enf.ENFActlVisitor;

/**
 * @author loreti
 *
 */
public class CTLGlobalBackwardModelChecker<X extends StateInterface,Y extends ActionInterface> implements ENFActlVisitor<X, Y, Set<X>>{

	private Graph<X, Y> graph;
	
	public CTLGlobalBackwardModelChecker( Graph<X,Y> graph ) {
		this.graph = graph;
	}
	
	
	@Override
	public Set<X> visit(True<X, Y> f) {
		return graph.getStates();
	}

	@Override
	public Set<X> visit(And<X, Y> f) {
		Set<X> leftSat = f.getLeft().accept(this);
		Set<X> rightSat = f.getRight().accept(this);
		leftSat.retainAll(rightSat);
		return leftSat;
	}

	@Override
	public Set<X> visit(Not<X, Y> f) {
		Set<X> result = graph.getStates();
		Set<X> satArg = f.getArgument().accept(this);
		result.removeAll(satArg);
		return result;
	}

	@Override
	public Set<X> visit(AtomicPredicate<X, Y> f) {
		Set<X> result = new TreeSet<X>();
		for (X x : graph.getStates()) {
			if (f.eval(x)) {
				result.add(x);
			}
		}
		return result;
	}

	@Override
	public Set<X> visit(ExistsNext<X, Y> f) {
		Set<X> nextSat = f.getStateFormula().accept(this);
		Set<X> result = new TreeSet<X>();
		for (X x : nextSat) {
			Set<X> preset = graph.getPreset(x,f.getLabelPredicate());
			result.addAll(preset);
 		}
		return result;
	}

	@Override
	public Set<X> visit(ExistsUntil<X, Y> f) {
		Set<X> tSet = f.getRightArgument().accept(this);
		Set<X> eSet = new HashSet<X>(tSet);
		Set<X> satLeft = f.getLeftArgument().accept(this);
		satLeft.removeAll(tSet);
		while (!eSet.isEmpty()) {
			Set<X> tmpOpenSet = new TreeSet<X>();
			for (X x : eSet) {
				Set<X> preset = graph.getPreset(x,f.getLabelPredicate());
				for (X x2 : preset) {
					if (satLeft.contains(x2)) {
						satLeft.remove(x2);
						tmpOpenSet.add(x2);
						tSet.add(x2);
					}
				}
			}
			eSet = tmpOpenSet;
		}
		return tSet;
	}

	@Override
	public Set<X> visit(ExistsAlways<X, Y> f) {
		Set<X> satSet = f.getAlwaysFormula().accept(this);
		HashMap<X, Integer> postSetCount =  new HashMap<X, Integer>();
		for (X x : satSet) {
			postSetCount.put(x, graph.getPostset(x, f.getLabelPredicate()).size());
		}
		Set<X> eSet = graph.getStates();
		eSet.retainAll(satSet);
		while (!eSet.isEmpty()) {
			Set<X> tmpSet = new HashSet<X>();
			for (X x : eSet) {
				Set<X> preset = graph.getPreset(x,f.getLabelPredicate());
				for (X x2 : preset) {
					if (satSet.contains(x2)) {
						int count = postSetCount.get(x2);
						count--;
						postSetCount.put(x2, count);
						if (count == 0) {
							satSet.remove(x2);
							tmpSet.add(x2);
						}
					}
				}
			}
			eSet = tmpSet;
		}
		return satSet;
	}

}
