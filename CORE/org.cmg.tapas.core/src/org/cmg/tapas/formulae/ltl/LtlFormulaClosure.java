/**
 * 
 */
package org.cmg.tapas.formulae.ltl;

import java.util.HashSet;
import java.util.Set;

import org.cmg.tapas.formulae.ltl.LtlFormula.And;
import org.cmg.tapas.formulae.ltl.LtlFormula.Atomic;
import org.cmg.tapas.formulae.ltl.LtlFormula.False;
import org.cmg.tapas.formulae.ltl.LtlFormula.Next;
import org.cmg.tapas.formulae.ltl.LtlFormula.Not;
import org.cmg.tapas.formulae.ltl.LtlFormula.Or;
import org.cmg.tapas.formulae.ltl.LtlFormula.True;
import org.cmg.tapas.formulae.ltl.LtlFormula.Until;

/**
 * @author loreti
 *
 */
public class LtlFormulaClosure<S> implements LtlVisitor<S, Set<LtlFormula<S>>>{

	@Override
	public Set<LtlFormula<S>> visit(True<S> f) {
		return createAndAdd( f );
	}

	@Override
	public Set<LtlFormula<S>> visit(False<S> f) {
		return createAndAdd( f );
	}

	private Set<LtlFormula<S>> createAndAdd(LtlFormula<S> f) {
		HashSet<LtlFormula<S>> toReturn = new HashSet<LtlFormula<S>>();
		toReturn.add(f);
		toReturn.add(f.getNegation());
		return toReturn;
	}

	@Override
	public Set<LtlFormula<S>> visit(And<S> f) {
		Set<LtlFormula<S>> set = createAndAdd(f);
		set.addAll(f.getLeftArgument().acceptVisitor(this));
		set.addAll(f.getRightArgument().acceptVisitor(this));
		return set;
	}

	@Override
	public Set<LtlFormula<S>> visit(Or<S> f) {
		Set<LtlFormula<S>> set = createAndAdd(f);
		set.addAll(f.getLeftArgument().acceptVisitor(this));
		set.addAll(f.getRightArgument().acceptVisitor(this));
		return set;
	}

	@Override
	public Set<LtlFormula<S>> visit(Not<S> f) {
		Set<LtlFormula<S>> set = createAndAdd(f);
		set.addAll(f.getArgument().acceptVisitor(this));
		return set;
	}

	@Override
	public Set<LtlFormula<S>> visit(Until<S> f) {
		Set<LtlFormula<S>> set = createAndAdd(f);
		set.addAll(f.getLeftArgument().acceptVisitor(this));
		set.addAll(f.getRightArgument().acceptVisitor(this));
		return set;
	}

	@Override
	public Set<LtlFormula<S>> visit(Next<S> f) {
		Set<LtlFormula<S>> set = createAndAdd(f);
		set.addAll(f.getArgument().acceptVisitor(this));
		return set;
	}

	@Override
	public Set<LtlFormula<S>> visit(Atomic<S> f) {
		return createAndAdd( f );
	}

}
