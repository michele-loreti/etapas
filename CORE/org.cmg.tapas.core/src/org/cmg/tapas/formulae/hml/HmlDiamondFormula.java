/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.graph.filter.HashSetActionFilter;
import org.cmg.tapas.core.graph.filter.SingleActionFilter;
import org.cmg.tapas.core.graph.filter.TrueFilter;
import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.pa.Process;


/**
 * @author Michele Loreti
 *
 */
public final class HmlDiamondFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private Filter<Y> actions;
	private HmlFormula<X,Y> arg;
	
	
	public HmlDiamondFormula( HmlFormula<X,Y> arg ) {
		this(new TrueFilter<Y>(),arg);
	}
	
	public HmlDiamondFormula( Filter<Y> actions , HmlFormula<X,Y> arg ) {
		this.actions = actions;
		this.arg = arg;
	}
	
	public HmlDiamondFormula( Y action , HmlFormula<X, Y> arg) { 
		this( new SingleActionFilter<Y>(action) , arg );
	}
	
	public HmlDiamondFormula(Set<Y> action_set, HmlFormula<X, Y> arg) {
		this( new HashSetActionFilter<Y>(action_set) , arg );
	}

	/* (non-Javadoc)
	 * @see org.cmg.lmc.Formula#satisfies(graph.Graph)
	 */
	public boolean satisfies( X x ) {
		
		HashMap<Y, Set<X>> poset = x.getNext();

		for (Y a : poset.keySet()) {
			if (actions.check(a)) {
				for (X s : poset.get(a)) {
					if (arg.satisfies(s)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Proof<X, Y> getProof(X x) {
		HashMap<Y, Set<X>> poset = x.getNext(  );
		Vector<Proof<X, Y>> proofs = new Vector<Proof<X, Y>>();
		Proof<X,Y> p;
		for (Y a : poset.keySet()) {
			if (actions.check(a)) {
				for (X s : poset.get(a)) {
					p = arg.getProof(s);
					if (p.isSuccess()) {
						return new Proof<X,Y>( x , this , new Object[] { p }  , actions );
					}
					proofs.add(p);
				}
			}
		}		
		return new Proof<X,Y>( x , this , proofs.toArray() , actions , false );
	}

	
	@Override
	protected HmlFormula<X, Y> doClone() {
		return new HmlDiamondFormula<X, Y>(actions,arg.clone());
	}

	@Override
	protected void doVisit(FormulaVisitor<X,Y> visitor) {
		visitor.visitDiamond( this );
	}

	@Override
	protected String _toString() {
		return "<"+actions+">"+arg;
	}

	@Override
	public String getUnicode() {
		return "<"+actions+">"+arg.getUnicode();
	}

	@Override
	public String operator() {
		return "DIAMOND( "+actions+" )";	
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlDiamondFormula) {
			HmlDiamondFormula<X, Y> f = (HmlDiamondFormula<X, Y>) obj;
			
			if(!actions.equals(f.actions))
				return false;
			
			return arg.equals(f.arg);
			
		}
		
		return false;
	}
}
