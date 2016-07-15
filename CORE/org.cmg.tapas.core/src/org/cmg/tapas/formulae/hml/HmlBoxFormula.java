/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.graph.filter.HashSetActionFilter;
import org.cmg.tapas.core.graph.filter.SingleActionFilter;
import org.cmg.tapas.core.graph.filter.TrueFilter;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public final class HmlBoxFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private Filter<Y> filter;
	private HmlFormula<X,Y> arg;
	
	
	public HmlBoxFormula( HmlFormula<X,Y> arg ) {
		this(new TrueFilter<Y>(),arg);
	}
		
	public HmlBoxFormula(Filter<Y> actions , HmlFormula<X,Y> arg) {
		this.filter = actions;
		this.arg = arg;
	}
	
	public HmlBoxFormula( Y action , HmlFormula<X, Y> arg) { 
		this( new SingleActionFilter<Y>(action) , arg );
	}
	
	public HmlBoxFormula(Set<Y> action_set, HmlFormula<X, Y> arg) {
		this( new HashSetActionFilter<Y>(action_set) , arg );
	}

	
	/* (non-Javadoc)
	 * @see org.cmg.lmc.Formula#satisfies(graph.Graph)
	 */
	public boolean satisfies( X x ) {
		
		HashMap<Y, Set<X>> poset = x.getNext();

			for (Y a : poset.keySet() ) {
				if (filter.check(a)) {
					for (X s: poset.get(a)) {
						if (!arg.satisfies(s)) {
							return false;
						}
					}
				}
			}
		return true;

	}
	
	@Override
	public Proof<X, Y> getProof(X x) {
		HashMap<Y, Set<X>> poset = x.getNext(  );
		Vector<Proof<X, Y>> proofs = new Vector<Proof<X, Y>>();
		Proof<X,Y> p;
			for (Y a : poset.keySet()) {
				if (filter.check(a)) {
					for (X s : poset.get(a)) {
						p = arg.getProof(s);
						if (!p.isSuccess()) {
							return new Proof<X,Y>( x , this , new Object[] { p } , filter, false );
						}
						proofs.add(p);
					}
				}
			}
		return new Proof<X,Y>( x , this , proofs.toArray() , filter );
	}


	@Override
	protected HmlFormula<X, Y> doClone() {
		return new HmlBoxFormula<X, Y>(filter,arg.clone());
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitBox(this);
	}

	@Override
	protected String _toString() {
		return "["+filter+"]"+arg;
	}

	@Override
	public String getUnicode() {
		return "["+filter+"]"+arg.getUnicode();
	}

	@Override
	public String operator() {
		return "BOX( "+filter+" )";
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlBoxFormula) {
			HmlBoxFormula<X, Y> f = (HmlBoxFormula<X, Y>) obj;
			
			if(!filter.equals(f.filter))
				return false;
			
			return arg.equals(f.arg);
			
		}
		
		return false;
	}
}
