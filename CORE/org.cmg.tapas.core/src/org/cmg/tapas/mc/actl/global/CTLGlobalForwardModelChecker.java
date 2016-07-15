/**
 * 
 */
package org.cmg.tapas.mc.actl.global;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.core.graph.StateInterface;
import org.cmg.tapas.mc.actl.global.enf.ENFActlFormula;
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
public class CTLGlobalForwardModelChecker<X extends StateInterface,Y extends ActionInterface> implements ENFActlVisitor<X, Y, Set<X>>{

	private Graph<X, Y> graph;
	
	public CTLGlobalForwardModelChecker( Graph<X,Y> graph ) {
		this.graph = graph;
	}
	
	public Set<X> sat(ENFActlFormula<X, Y> f) {
		return f.accept(this);
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
		Set<X> result = new HashSet<X>();
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
		Set<X> result = new HashSet<X>();
		for (X x : graph.getStates()) {
			Set<X> poset = graph.getPostset(x,f.getLabelPredicate());
			poset.retainAll(nextSat);
			if (!poset.isEmpty()) {
				result.add(x);
			}
 		}
		return result;
	}

	@Override
	public Set<X> visit(ExistsUntil<X, Y> f) {
		boolean isChanged = true;
		Set<X> satSet = f.getRightArgument().accept(this);
		Set<X> openSet = f.getLeftArgument().accept(this);
		while (isChanged) {
			isChanged = false;
			Set<X> tmpSet = new TreeSet<X>(satSet);
			Set<X> tmpOpenSet = new TreeSet<X>();
			for (X x : openSet) {
				Set<X> poset = graph.getPostset(x,f.getLabelPredicate());
				poset.retainAll(satSet);
				if (!poset.isEmpty()) {
					tmpSet.add(x);
					isChanged = true;
				} else {
					tmpOpenSet.add(x);
				}
			}
			satSet = tmpSet;
			openSet = tmpOpenSet;
		}
		return satSet;
	}

	@Override
	public Set<X> visit(ExistsAlways<X, Y> f) {
		boolean isChanged = true;
		Set<X> satSet = f.getAlwaysFormula().accept(this);
		while (isChanged) {
			isChanged = false;
			Set<X> tmpSet = new TreeSet<X>();
			for (X x : satSet) {
				Set<X> poset = graph.getPostset(x,f.getLabelPredicate());
				poset.retainAll(satSet);
				if (!poset.isEmpty()) {
					tmpSet.add(x);
				} else {
					isChanged = true;
				}
			}
			satSet = tmpSet;
		}
		return satSet;
	}

}
