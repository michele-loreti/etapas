package org.cmg.tapas.formulae.hml;


import java.util.LinkedList;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;

/**
 * @author calzolai
 *
 */
public class HmlDivFormulae<X extends Process<X, Y>,Y extends ActionInterface>
				extends HmlFormula<X, Y> {
	
	private String toString = "DIV";
	private HmlFormula<X, Y> div;
	private Y tauAct;
	
	public HmlDivFormulae(Y tauAct){
		this.tauAct= tauAct;
		div = divFormula();
	}
	
	private HmlFormula<X, Y> divFormula(){
		// max X.(<tau> true & [tau] X)
		String name = "X";
		HmlFixPoint<X, Y> fix = new HmlFixPoint<X, Y>(true, name);

		LinkedList<HmlFormula<X, Y>> subList = new LinkedList<HmlFormula<X, Y>>();

		HmlFormula<X, Y> f = new HmlDiamondFormula<X, Y>(tauAct, new HmlTrue<X, Y>());
		subList.add(f);
		f = new HmlBoxFormula<X, Y>(tauAct, fix.getReference());
		subList.add(f);

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
		div.doVisit(visitor);		
	}

	@Override
	protected boolean _equals(Object o) {
		return div.equals(o);
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
