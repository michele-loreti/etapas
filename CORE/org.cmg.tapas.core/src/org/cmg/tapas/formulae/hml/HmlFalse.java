/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public final class HmlFalse<X extends Process<X, Y>,Y extends ActionInterface> 
					  extends HmlFormula<X,Y> {

	public boolean satisfies(X x ) {
		return false;
	}

	@Override
	protected HmlFormula<X, Y> doClone() {
		return this;
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitFalse(this);
	}

	@Override
	protected String _toString() {
		return "false";
	}

	@Override
	public String getUnicode() {
		return "false";
	}

	@Override
	public Proof<X, Y> getProof(X s) {
		return new Proof<X,Y>( s , this , false );
	}

	@Override
	public String operator() {
		return "";
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlFalse) 
			return true;
		else return false;
	}
}
