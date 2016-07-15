/**
 * 
 */
package org.cmg.tapas.formulae.hml;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.pa.Process;

/**
 * @author loreti
 *
 */
public class HmlAtomic<X extends Process<X, Y>,Y extends ActionInterface> extends HmlFormula<X, Y>{

	private Filter<X> filter;

	public HmlAtomic( Filter<X> filter ) {
		this.filter = filter;
	}
	
	@Override
	public boolean satisfies(X s) {
		return filter.check(s);
	}

	@Override
	public Proof<X, Y> getProof(X s) {
		return null;
	}

	@Override
	protected HmlFormula<X, Y> doClone() {
		return new HmlAtomic<X,Y>(filter);
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitAtomic(this);
	}

	@Override
	protected String _toString() {
		return filter.toString();
	}

	@Override
	public String getUnicode() {
		return filter.toString();
	}

	@Override
	public String operator() {
		return "ATOMIC";
	}

	@Override
	protected boolean _equals(Object o) {
		if (o instanceof HmlAtomic<?, ?>) {
			return filter.equals(((HmlAtomic<?,?>) o).filter);
		}
		return false;
	}

}
