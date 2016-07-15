package org.cmg.tapas.formulae.actl;


import java.util.HashSet;
import java.util.Set;


public abstract class PathFormula<X,Y> 
						implements Cloneable {

	public Set<X> hypothesis;
	
	public final PathFormula<X,Y> clone() {
		return doClone();
	}

	public void visit(ActlPathFormulaVisitor<X,Y> visitor) {
		doVisit(visitor);
	}

	@Override
	public String toString() {
		return _toString();
	}

	@Override
	public int hashCode() {
		return (toString()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj+"");
	}

	protected abstract PathFormula<X, Y> doClone();
	protected abstract void doVisit(ActlPathFormulaVisitor<X,Y> visitor);
	protected abstract String _toString();
	public abstract String getUnicode();
	public abstract String operator();

	public void reset() {
		hypothesis = new HashSet<X>();
	}

}
