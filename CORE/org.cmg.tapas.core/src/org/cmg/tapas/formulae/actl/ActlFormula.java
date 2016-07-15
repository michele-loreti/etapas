package org.cmg.tapas.formulae.actl;


import org.cmg.tapas.core.graph.ActionInterface;


public abstract class ActlFormula<X,Y> implements Cloneable {

	private String description;
	private String name =""; 
	
	public final ActlFormula<X,Y> clone() {
		return doClone();
	}

	public void visit( ActlFormulaVisitor<X,Y> visitor ) {
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

	public String getName() {
		return name ;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	protected abstract ActlFormula<X, Y> doClone();
	protected abstract void doVisit(ActlFormulaVisitor<X,Y> visitor);
	protected abstract String _toString();
	public abstract String getUnicode();
	public abstract String operator();

}

