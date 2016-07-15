/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.HashSet;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public final class HmlFixPoint<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private HmlFormula<X,Y> arg;
	private boolean isMax;
	private boolean cloning = false;
	private HmlFixPoint<X,Y> clone;
	private HashSet<X> tableaux;
	private String var;
	private HashSet<X> cacheSat = new HashSet<X>();
	private HashSet<X> cacheUnsat  = new HashSet<X>();

	public HmlFixPoint( String var ) {
		this(true , var );
	}
	
	public HmlFixPoint( boolean isMax , String var ) {
		this(new HmlTrue<X, Y>() , isMax , var );
	}
	
	public HmlFixPoint( HmlFormula<X,Y> arg , boolean isMax , String var ) {
		this.arg = arg;
		this.isMax = isMax;
		this.tableaux = new HashSet<X>();
		this.var = var;
	}

	public boolean satisfies( X x ) {
		if (cacheSat.contains(x)) {
			return true;
		}
		if (cacheUnsat.contains(x)) {
			return false;
		}
		if (tableaux.contains(x)) {
			return isMax;
		}
		HmlFixPoint<X, Y> f = (HmlFixPoint<X, Y>) clone();
		f.tableaux.add(x);
		f.cacheSat = cacheSat;
		f.cacheUnsat = cacheUnsat;
		boolean flag = f.arg.satisfies(x);
		if (flag) {
			cacheSat.add(x);
		} else {
			cacheUnsat.add(x);
		}
		return flag;
	}

	public Proof<X,Y> getProof( X x ) {
		if (tableaux.contains(x)) {
			return new Proof<X, Y>( x , this , isMax );
		}
		HmlFixPoint<X, Y> f = (HmlFixPoint<X, Y>) clone();
		f.tableaux.add(x);
		Proof<X, Y> p = f.arg.getProof(x);
		return new Proof<X, Y>(x,this, new Object[] { p } , p.isSuccess() );
	}

	public void setSubformula( HmlFormula<X,Y> f ) {
		this.arg = f ;
	}
	
	@Override
	protected HmlFormula<X, Y> doClone() {
		
		synchronized (this) {
			while (cloning) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}		
			cloning = true;
		}
		clone = new HmlFixPoint<X, Y>(isMax,var);
		this.setCloning(true);
		HmlFormula<X,Y> f = arg.clone();
		clone.isMax = isMax;
		clone.tableaux = (HashSet<X>) tableaux.clone();
		clone.arg = f; 
		HmlFixPoint<X, Y> toReturn = clone;
		toReturn.cacheSat = cacheSat;
		toReturn.cacheUnsat = cacheUnsat;
		synchronized (this) {
			cloning = false;
			clone = null;
			notifyAll();
		}
		return toReturn;
	}
	
	private synchronized HmlFixPoint<X, Y> getClone() {
		return clone;
	}
	
	private synchronized boolean isCloning() {
		return cloning;
	}

	private synchronized void setCloning( boolean cloning ) {
		this.cloning = cloning;
	}
	
	public HmlFormula<X,Y> getReference() {
		return new Reference();
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlFixPoint) {
			HmlFixPoint<X, Y> f = (HmlFixPoint<X, Y>) obj;
			
			if(isMax != f.isMax ||
					var != f.var)
				return false;
			
			return arg.equals(f.arg);
		}
		
		return false;
	}
	
	public class Reference extends HmlFormula<X,Y> {
		
		private Reference() {
			
		}
		
		/* (non-Javadoc)
		 * @see org.cmg.lmc.Formula#satisfies(graph.Graph)
		 */
		public boolean satisfies(X x ) {
			return HmlFixPoint.this.satisfies(x);
		}

		@Override
		protected HmlFormula<X, Y> doClone() {
			if (isCloning()) {
				return getClone().getReference();
			}
			return new Reference();
		}

		@Override
		protected void doVisit(FormulaVisitor<X, Y> visitor) {
			visitor.visitReference(this);
		}

		@Override
		protected String _toString() {
			return var;
		}

		@Override
		public String getUnicode() {
			return var;
		}

		@Override
		public Proof<X, Y> getProof(X s) {
			return HmlFixPoint.this.getProof(s);
		}

		@Override
		public String operator() {
			return (isMax ? "MAX" : "MIN");
		}
		
		@Override
		public boolean _equals(Object obj) {
			if(obj == null)
				return false;
			
			if (obj instanceof HmlFixPoint.Reference) { 
				Reference f = (Reference) obj;
								
				return equals(f);			
			}
			
			return false;
		}
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitFixPoint(this);
	}

	@Override
	protected String _toString() {
		return (isMax?"max ":"min ")+var+". "+arg;
	}

	@Override
	public String getUnicode() {
		return (isMax?"\u03BD ":"\u03BC ")+var+". "+arg.getUnicode();
	}
	
	@Override
	public String operator() {
		return (isMax ? "MAX" : "MIN");
	}
	
	public void setVar(String name){
		this.var = name;
	}
}
