/**
 * 
 */
package org.cmg.tapas.formulae.actl.state;


import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.actl.ActlFormulaVisitor;


/**
 * @author loreti
 *
 */
public final class ActlNot<X,Y> extends ActlFormula<X,Y> {

	private ActlFormula<X,Y> arg;
	
	public ActlNot( ActlFormula<X,Y> arg ) {
		this.arg = arg;
	}

	@Override
	protected ActlFormula<X, Y> doClone() {
		return new ActlNot<X, Y>(arg.clone());
	}

	@Override
	protected void doVisit(ActlFormulaVisitor<X, Y> visitor) {
		visitor.visitNotFormula(this);
	}

	@Override
	protected String _toString() {
		return "! "+arg;
	}

	@Override
	public String getUnicode() {
		return "\u00AC "+arg.getUnicode();
	}
	
	@Override
	public String operator() {
		return "NOT";	
	}

	public ActlFormula<X, Y> getFormula() {
		return arg;
	}

}
