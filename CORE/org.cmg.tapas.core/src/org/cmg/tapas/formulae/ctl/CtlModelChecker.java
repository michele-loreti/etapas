/**
 * 
 */
package org.cmg.tapas.formulae.ctl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.Graph;
import org.cmg.tapas.formulae.ctl.StateFormula.And;
import org.cmg.tapas.formulae.ctl.StateFormula.Atomic;
import org.cmg.tapas.formulae.ctl.StateFormula.False;
import org.cmg.tapas.formulae.ctl.StateFormula.Next;
import org.cmg.tapas.formulae.ctl.StateFormula.Not;
import org.cmg.tapas.formulae.ctl.StateFormula.Or;
import org.cmg.tapas.formulae.ctl.StateFormula.True;
import org.cmg.tapas.formulae.ctl.StateFormula.Until;

/**
 * @author loreti
 *
 */
public class CtlModelChecker<S> implements CtlVisitor<S, Set<S>> {
	
	private Graph<S, ?> lts;

	private CtlModelChecker( Graph<S,?> lts ) {
		this.lts = lts;
	}

	@Override
	public Set<S> visit(True<S> f) {
		return lts.getStates();
	}

	@Override
	public Set<S> visit(False<S> f) {
		return new HashSet<S>();
	}

	@Override
	public Set<S> visit(And<S> f) {
		Set<S> leftResult = f.getLeftArgument().accept(this);
		if (leftResult.isEmpty()) {
			return leftResult;
		}
		Set<S> rightResult = f.getRightArgument().accept(this);
		if (rightResult.isEmpty()) {
			return rightResult;
		}
		return intersection( leftResult , rightResult );
	}

	@Override
	public Set<S> visit(Or<S> f) {
		Set<S> leftResult = f.getLeftArgument().accept(this);
		Set<S> rightResult = f.getRightArgument().accept(this);
		return union( leftResult , rightResult );
	}

	@Override
	public Set<S> visit(Not<S> f) {
		Set<S> negatedResult = f.getArgument().accept(this);
		if (negatedResult.isEmpty()) {
			return lts.getStates();
		}
		if (negatedResult.size() == lts.getNumberOfStates()) {
			return new HashSet<S>();
		}
		return negation( negatedResult );
	}

	private Set<S> negation(Set<S> A) {
		Set<S> X = lts.getStates();
		X.removeAll(A);
		return X;
	}

	@Override
	public Set<S> visit(Until<S> f) {
		Set<S> rightResult = f.getRightArgument().accept(this);
		if (rightResult.isEmpty()) {
			return rightResult;
		}
		Set<S> leftResult = f.getLeftArgument().accept(this);
		if (leftResult.isEmpty()) {
			return rightResult;
		}
		if (f.isExistential()) {
			return satExistsUntil( leftResult , rightResult );
		} else {
			Set<S> notPsi = negation( rightResult );
			Set<S> notPhi = negation( leftResult );
			Set<S> existsAlwaysNotPsi = satExitsAlways( notPsi );
			Set<S> existsNotPsiUntilNotPsiAndNotPhi = satExistsUntil( notPsi , intersection( notPhi, notPsi ) );
			return intersection( negation( existsNotPsiUntilNotPsiAndNotPhi ) , negation( existsAlwaysNotPsi ) );
		}
	}

	private Set<S> satExitsAlways(Set<S> A) {
		Set<S> E = lts.getStates();
		E.removeAll(A);
		HashMap<S,Integer> p = new HashMap<S, Integer>();
		for (S s : A) {
			p.put(s, lts.getPostset(s).size());
		}
		while (!E.isEmpty()) {
			HashSet<S> E2 = new HashSet<S>();
			for (S s1 : E) {
				for (Set<S> preSet : lts.getPresetMapping(s1).values()) {
					for (S s2 : preSet) {
						if (A.contains(s2)) {
							Integer count = p.get(s2);
							count = count - 1;
							if (count == 0) {
								A.remove(s2);
								E2.add(s2);
							}
						}
					}
				}
			}
			E = E2;
		}
		return A;
	}

	private Set<S> satExistsUntil(Set<S> A, Set<S> B) {
		Set<S> E = B;
		HashSet<S> T = new HashSet<S>( E );
		while ( !E.isEmpty() ) {
			Set<S> E2 = new HashSet<S>();
			for (S s : E) {
				Map<?,? extends Set<S>> nextMap = lts.getPresetMapping(s);
				for (Set<S> X : nextMap.values()) {
					for (S s2 : X) {
						if (A.contains(s2) && !T.contains(s2)) {
							E2.add(s2);
							T.add(s2);
						}
					}
				}
			}
		}
		return T;
	}

	@Override
	public Set<S> visit(Next<S> f) {
		if (f.isExistential()) {
			return satExistsNext( f.getArgument().accept(this) );
		} else {
			return negation( satExistsNext( negation(  f.getArgument().accept(this) ) ) );
		}
	}

	private Set<S> satExistsNext(Set<S> satS ) {
		HashSet<S> results = new HashSet<S>();
		for (S s : satS) {
			Map<?, ? extends Set<S>> presetMap = lts.getPresetMapping(s);
			for (Set<S> preset : presetMap.values() ) {
				results.addAll(preset);
			}
		}
		return results;
	}

	@Override
	public Set<S> visit(Atomic<S> f) {
		HashSet<S> result = new HashSet<S>();
		for ( S s:lts.states() ) {
			if (f.eval(s)) {
				result.add(s);
			}
		}
		return result;
	}
	
	private Set<S> intersection(Set<S> A, Set<S> B) {
		HashSet<S> result = new HashSet<S>();
		Set<S> iteratorSet = A;
		Set<S> constraintSet = B;
		if (A.size()>B.size()) {
			iteratorSet = B;
			constraintSet = A;
		}
		for (S s : iteratorSet) {
			if (constraintSet.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}

	private Set<S> union(Set<S> A, Set<S> B) {
		HashSet<S> result = new HashSet<S>(A);
		result.addAll(B);
		return result;
	}



}
