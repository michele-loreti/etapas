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
public final class ActlAnd<X,Y> extends ActlFormula<X,Y> {

	private ActlFormula<X,Y> left;
	private ActlFormula<X,Y> right;
	
	public ActlAnd( ) {
		this(new ActlFalse<X, Y>() , new ActlFalse<X, Y>());
	}
	
	public ActlAnd( ActlFormula<X,Y> left , ActlFormula<X,Y> right ) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	protected ActlFormula<X, Y> doClone() {
		return new ActlAnd<X, Y>(left.clone() , right.clone());
	}

	@Override
	protected void doVisit(ActlFormulaVisitor<X, Y> visitor) {
		visitor.visitAndFormula(this);
	}

	@Override
	protected String _toString() {
		String toReturn = "";
		toReturn += "( "+left.toString()+" )";
	  toReturn += " & ( "+right.toString()+" )";
		return toReturn;
	}

	@Override
	public String getUnicode() {
		String toReturn = "";
		toReturn += "( "+left.getUnicode()+" )";
	  toReturn += " \u22c0  ( "+right.getUnicode()+" )";
		return toReturn;
	}

	@Override
	public String operator() {
		return "AND";
	}

	public ActlFormula<X, Y> getLeft() {
		return left;
	}

	public ActlFormula<X, Y> getRight() {
		return right;
	}
}
