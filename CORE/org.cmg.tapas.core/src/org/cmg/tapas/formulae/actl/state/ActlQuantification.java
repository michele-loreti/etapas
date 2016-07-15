package org.cmg.tapas.formulae.actl.state;



import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.actl.ActlFormulaVisitor;
import org.cmg.tapas.formulae.actl.PathFormula;


public class ActlQuantification<X,Y> 
					extends ActlFormula<X,Y> {
	
	private boolean isForAll;
	private PathFormula<X, Y> formula;
	
	public ActlQuantification(boolean forAll, PathFormula<X, Y> formula) {
		this.isForAll = forAll;
		this.formula = formula;
	}
	
	public PathFormula<X, Y> getPathFormula(){
		return formula;
	}

	@Override
	protected String _toString() {
		if(isForAll)
			return "A"+formula.toString();
		else return "E"+formula.toString();
	}

	@Override
	protected ActlFormula<X,Y> doClone() {
		return new ActlQuantification<X, Y>(isForAll, formula.clone());
	}

	@Override
	protected void doVisit(ActlFormulaVisitor<X,Y> visitor) {
		visitor.visitQuantificationFormula(this);
	}

	@Override
	public String getUnicode() {
		String res = "";
		if(isForAll)
			res = "\u2200 ";
		else res = "\u2203 ";
		return res+formula.getUnicode();
	}

	@Override
	public String operator() {
		return (isForAll ? "ALL" : "EXIST");
	}
	
	public boolean isForall(){
		return isForAll;
	}

}
