/**
 * 
 */
package org.cmg.tapas.formulae.lmc;


import org.cmg.tapas.formulae.hml.HmlFormula;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public class Proof<X extends Process<X, Y>,Y extends ActionInterface> {

	private X state;
	private HmlFormula<X, Y> formula;
	private boolean success;
	private Object[] assertions;
	private Filter<Y> actions;
	
	/**
	 * @return the state
	 */
	public X getState() {
		return state;
	}

	/**
	 * @return the formula
	 */
	public HmlFormula<X, Y> getFormula() {
		return formula;
	}

	/**
	 * @return the assertions
	 */
	public Object[] getAssertions() {
		return assertions;
	}

	public Proof( X s , HmlFormula<X,Y> f ) {
		this( s , f , null , null , true );
	}

	public Proof( X s , HmlFormula<X,Y> f , boolean success ) {
		this( s , f , null , null , success );
	}

	public Proof( X s , HmlFormula<X,Y> f , Object[] steps ) {
		this( s , f , steps , null , true );
	}
	
	public Proof( X s , HmlFormula<X,Y> f , Object[] steps , Filter<Y> actions ) {
		this( s , f , steps, actions , true );
	}

	public Proof(X s , HmlFormula<X,Y> f, Object[] steps, boolean success) {
		this( s , f , steps, null, success);
	}
	
	public Proof(X s , HmlFormula<X,Y> f, Object[] steps, Filter<Y> actions , boolean success) {
		this.state = s;
		this.formula = f;
		this.assertions = steps;
		this.success = success;
		this.actions = actions;
	}


	public boolean isSuccess() {
		return success;
	}
	
	public Filter<Y> getActions() {
		return actions;
	}
	
	private String _getActions(){
		String res = "";		
		if(actions != null)
			res += "<"+actions+">";
		if(assertions != null && assertions.length > 0){
			for (Object o : assertions) {
				if(o instanceof Proof){
					Proof p = (Proof) o;
					res += p._getActions();
				}else{
					res += "#ERROR";
				}
			}
		}
		
		return res;
	}
	
	public String getActionsList(){
		return _getActions()+"\n";
	}
	
	private String toFormattedString(int l){
		String space = "--+";
		String tab = "";
		for (int i = 0; i < l; i++) {
			tab += space;
		}
		
		String res = tab+"Proof: "+state.toString()+"\n";
		
		if(actions != null)
			res += tab+"Actions: "+actions+"\n";
		if(assertions != null && assertions.length > 0){
			res += tab+"Assertion:\n";
			for (Object o : assertions) {
				if(o instanceof Proof){
					Proof p = (Proof) o;
					res += p.toFormattedString(l+1);
				}else{
					res += o;
				}
			}
		}
		return res;
	}
	
	public String toString(){
		return toFormattedString(0);
	}

}
