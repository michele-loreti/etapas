package org.cmg.tapas.formulae.hml;


import java.util.LinkedList;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.graph.filter.SingleActionFilter;
import org.cmg.tapas.pa.Process;


/**
 * @author calzolai
 *
 */
public class HmlDivergenceFormula<X extends Process<X, Y>,Y extends ActionInterface> 
			extends HmlFormula<X,Y> {

	private String toString = "DIV";
	private HmlFormula<X, Y> div;
	private Y tauAction;
	
	public HmlDivergenceFormula(Y tauAction){
		this.tauAction = tauAction; 
		div = divFormula();
		
	}
	
	private HmlFormula<X, Y> divFormula(){
		// max X.(
		// <tau> true & <tau> X
		// )
		String name = "X";
		HmlFixPoint<X, Y> fix = new HmlFixPoint<X, Y>(true, name);

		LinkedList<HmlFormula<X, Y>> subList = new LinkedList<HmlFormula<X, Y>>();

		Filter<Y> f = new SingleActionFilter<Y>(tauAction);
		subList.add(new HmlDiamondFormula<X, Y>(f, new HmlTrue<X, Y>()));

		HmlFormula<X, Y> formula = new HmlDiamondFormula<X, Y>(tauAction, fix.getReference());
		subList.add(formula);

		HmlAndFormula<X, Y> and = new HmlAndFormula<X, Y>(subList);
		fix.setSubformula(and);
		return fix;
	}
	
	@Override
	protected String _toString() {
		return toString;
	}

	@Override
	protected HmlFormula<X, Y> doClone() {
		return div.doClone();
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitDiv(this);
	}

	@Override
	protected boolean _equals(Object o) {
		if(o == null)
			return false;
		
		if (o instanceof HmlDivergenceFormula){
			HmlDivergenceFormula f = (HmlDivergenceFormula) o;
			return tauAction.equals(f.tauAction);
		}else 
			return false;
	}

	@Override
	public Proof<X, Y> getProof(X s) {
		return div.getProof(s);
	}

	@Override
	public String getUnicode() {
		return toString;
	}

	@Override
	public String operator() {
		return "";
	}

	@Override
	public boolean satisfies(X s) {
		return div.satisfies(s);
	}

}
