/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public final class HmlOrFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private LinkedList<HmlFormula<X,Y>> elements;
	
	public HmlOrFormula(  ) {
		this(new LinkedList<HmlFormula<X, Y>>());
	}
	
	public HmlOrFormula( HmlFormula<X,Y> left , HmlFormula<X,Y> right ) {
		this();
		this.elements.add(left);
		this.elements.add(right);
	}

	public HmlOrFormula(LinkedList<HmlFormula<X, Y>> elements) {
		this.elements = elements;
	}

	public HmlOrFormula(Collection<HmlFormula<X, Y>> elements) {
		this.elements = new LinkedList<HmlFormula<X,Y>>(elements);
	}

	/* (non-Javadoc)
	 * @see org.cmg.lmc.Formula#satisfies(graph.Graph)
	 */
	public boolean satisfies(X x ) {
		for (HmlFormula<X,Y> f : elements) {
			if (f.satisfies(x)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Proof<X, Y> getProof(X s) {
		Vector<Proof<X, Y>> proofs = new Vector<Proof<X, Y>>();
		Proof<X,Y> p;
		for (HmlFormula<X,Y> f : elements) {
			p = f.getProof(s);
			if (p.isSuccess()) {
				return new Proof<X,Y>( s , this , new Object[] { p } );
			}
			proofs.add(p);
		}
		return new Proof<X,Y>( s , this , proofs.toArray() , false );
	}

	
	@Override
	protected HmlFormula<X, Y> doClone() {
		LinkedList<HmlFormula<X,Y>> foo = new LinkedList<HmlFormula<X,Y>>();
		for (HmlFormula<X, Y> formula : elements) {
			foo.add(formula.clone());
		}
		
		return new HmlOrFormula<X, Y>(foo);
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitOr(this);
	}
	
	@Override
	protected String _toString() {
		if(elements.size() == 0) return "false";
		String toReturn = "";
		boolean flag = true;
		
		if(elements.size() > 1) toReturn = "(";
		for (HmlFormula<X, Y> formula : elements) {
			if ( flag ) {
			  toReturn += formula;
			  flag = false;
			} else {
			  toReturn += " or "+formula;
			}
		}
		if(elements.size() > 1) toReturn += ")";
		return toReturn;
	}

	@Override
	public String getUnicode() {
		if(elements.size() == 0) return "false";
		String toReturn = "";
		boolean flag = true;
		
		if(elements.size() > 1) toReturn = "(";
		for (HmlFormula<X, Y> formula : elements) {
			if ( flag ) {
			  toReturn += formula.getUnicode();
			  flag = false;
			} else {
			  toReturn += " \u22c1 "+formula.getUnicode();
			}
		}
		if(elements.size() > 1) toReturn += ")";
		return toReturn;
	}
	
	public void addSubFormula( HmlFormula<X,Y> f ) {
		elements.add(f);
	}

	public String operator() {
		return "OR";
	}
	
	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlOrFormula) {
			HmlOrFormula<X, Y> f = (HmlOrFormula<X, Y>) obj;
			
			if(elements.size() != f.elements.size())
				return false;
			
			return elements.containsAll(f.elements);			
		}
		
		return false;
	}
	
	public int getSF(){
		return elements.size();
	}
}
