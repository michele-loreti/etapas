/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public final class HmlNotFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private HmlFormula<X,Y> arg;
	
	public HmlNotFormula( HmlFormula<X,Y> arg ) {
		this.arg = arg;
	}
	
	@Override
	public boolean satisfies(X x ) {
		return !arg.satisfies(x);
	}

	@Override
	public Proof<X,Y> getProof(X x ) {
		Proof<X, Y> p = arg.getProof(x);
		return new Proof<X, Y>(x,this, new Object[] { p } , !p.isSuccess() );
	}


	@Override
	protected HmlFormula<X, Y> doClone() {
		return new HmlNotFormula<X, Y>(arg.clone());
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitNot(this);
	}

	@Override
	protected String _toString() {
		return "not "+arg;
	}

	@Override
	public String getUnicode() {
		return "\u00AC"+arg.getUnicode();
	}
	
	@Override
	public String operator() {
		return "NOT";	
	}


	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlNotFormula) {
			HmlNotFormula<X, Y> f = (HmlNotFormula<X, Y>) obj;
						
			return arg.equals(f.arg);			
		}
		
		return false;
	}

}
