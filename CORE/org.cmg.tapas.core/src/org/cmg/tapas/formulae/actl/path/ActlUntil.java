package org.cmg.tapas.formulae.actl.path;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.formulae.actl.ActlActionPredicate;
import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.actl.ActlPathFormulaVisitor;
import org.cmg.tapas.formulae.actl.PathFormula;

public class ActlUntil<X,Y> 
				extends PathFormula<X, Y> {

	private ActlFormula<X, Y> formula1;
	private Filter<Y> filter1;
	
	private Filter<Y> filter2;
	private ActlFormula<X, Y> formula2;
	
	
	public ActlUntil(ActlFormula<X, Y> f1
			, Filter<Y> chi1
			, ActlFormula<X, Y> f2) {
		super();
		this.formula1 = f1;
		this.filter1 = chi1;
		this.filter2 = null;
		this.formula2 = f2;
	}	
	
	public ActlFormula<X, Y> getFormula1() {
		return formula1;
	}

	public Filter<Y> getFilter1() {
		return filter1;
	}

	public Filter<Y> getFilter2() {
		return filter2;
	}

	public ActlFormula<X, Y> getFormula2() {
		return formula2;
	}

	public ActlUntil(ActlFormula<X, Y> f1
			, Filter<Y> chi1
			, Filter<Y> chi2
			, ActlFormula<X, Y> f2) {
		super();
		this.formula1 = f1;
		this.filter1 = chi1;
		this.filter2 = chi2;
		this.formula2 = f2;
	}

	@Override
	protected String _toString() {
		String res = " ("+formula1+") {"+filter1
					 +"} "+this.operator()+" ";
		
		if(filter2 != null)
			res += "{"+filter2+"} ";
		
		res += "("+formula2+") ";
		
		return res;
	}

	@Override
	protected PathFormula<X,Y> doClone() {
		return new ActlUntil<X, Y>(formula1.clone()
				, filter1, filter2
				, formula2.clone());
	}

	@Override
	public String getUnicode() {		
		String res = " ("+formula1.getUnicode()+") {"+filter1
		 +"} "+this.operator()+" ";

		if(filter2 != null) {
			res += "{"+filter2+"} ";
		}

		res += "("+formula2.getUnicode()+") ";

		return res;
	}

	@Override
	public String operator() {
		return "U";
	}


	@Override
	protected void doVisit(ActlPathFormulaVisitor<X, Y> visitor) {
		visitor.visitUntilFormula(this);
	}
}
