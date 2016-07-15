package org.cmg.tapas.formulae.actl.path;


import java.util.LinkedList;
import java.util.Map;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.formulae.actl.ActlActionPredicate;
import org.cmg.tapas.formulae.actl.ActlFormula;
import org.cmg.tapas.formulae.actl.ActlPathFormulaVisitor;
import org.cmg.tapas.formulae.actl.PathFormula;



public class ActlNext <X,Y> 
				extends PathFormula<X,Y> {

	private Filter<Y> actionFilter;
	private ActlFormula<X, Y> actlFormula;	
	
	public ActlNext(Filter<Y> filter, ActlFormula<X, Y> actlFormula) {
		super();
		this.actlFormula = actlFormula;
		this.actionFilter = filter;
	}
	


	@Override
	protected String _toString() {		
		return "X {"+actionFilter+"} "+actlFormula.toString();
	}

	@Override
	protected PathFormula<X,Y> doClone() {
		return new ActlNext<X, Y>(actionFilter, actlFormula.clone());
	}

	@Override
	protected void doVisit(ActlPathFormulaVisitor<X, Y> visitor) {
		visitor.visitNextFormula(this);
	}

	public Filter<Y> getFilter(){
		return actionFilter;
	}
	
	public ActlFormula<X, Y> getActlFormula(){
		return actlFormula;
	}

	@Override
	public String getUnicode() {
		String toReturn = "X";
		if (actionFilter != null) {
			toReturn += " {"+actionFilter+"} ";
		}
		return toReturn+actlFormula.getUnicode();
	}

	@Override
	public String operator() {
		return "X";
	}


}
