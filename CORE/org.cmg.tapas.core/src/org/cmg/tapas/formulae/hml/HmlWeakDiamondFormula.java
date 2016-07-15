/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.Set;

import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.core.graph.filter.HashSetActionFilter;
import org.cmg.tapas.core.graph.filter.SingleActionFilter;
import org.cmg.tapas.pa.Process;


/**
 * @author Calzolai
 *
 */
public final class HmlWeakDiamondFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private Filter<Y> filter;
	private HmlFormula<X,Y> arg;
	private Y tauAct;
	private HmlFixPoint<X, Y> equivalent;
	private boolean isEps;	
	
	public HmlWeakDiamondFormula(Filter<Y> actions, HmlFormula<X,Y> arg, Y tauAct) {
		this.arg = arg;
		this.tauAct = tauAct;
		this.filter = actions;
		
		int type = 1;		
		if(actions instanceof TauFilter)
			type = 0;
		else if(actions instanceof EpsFilter)
			type = 2;
			
		createEquivalent(type);
	}

	public HmlWeakDiamondFormula(HmlFormula<X,Y> arg, Y tauAct, boolean eps) {
		this.arg = arg;
		this.tauAct = tauAct;
		int type = 1;		
		if(eps){
			this.filter = new EpsFilter<Y>();
			type = 2;
		}else{
			this.filter = new TauFilter<Y>();
			type = 0;
		}
			
		createEquivalent(type);
	}

	public HmlWeakDiamondFormula(Y action , HmlFormula<X, Y> arg, Y tauAct) {
		this.arg = arg;
		this.tauAct = tauAct;		
		int type = 1;
		if(action.isTau()){
			this.filter = new TauFilter<Y>();
			type = 0;
		} else
			filter = new SingleActionFilter<Y>(action);
		
		createEquivalent(type);
	}
	
	public HmlWeakDiamondFormula(Set<Y> action_set, HmlFormula<X, Y> arg, Y tauAct) {
		this.arg = arg;
		this.tauAct = tauAct;
		int type = 1;	
		if(action_set == null || action_set.size() == 0){
			this.filter = new EpsFilter<Y>();
			type = 2;
		}else if(action_set.size() == 1){
			Y act = action_set.iterator().next();
			if(act.isTau()){
				this.filter = new TauFilter<Y>();
				type = 0;
			}
		}
		if(type == 1){
			this.filter = new HashSetActionFilter<Y>(action_set);
		}
		createEquivalent(type);
	}
	
	private HmlFixPoint<X,Y>  getEpsDiamond(){
		//min X.(<tau> X | arg)  = <tau>* arg
		String name = "X";
		HmlFixPoint<X, Y> fp = new HmlFixPoint<X, Y>(false, name);
		HmlDiamondFormula<X, Y> D1 = new HmlDiamondFormula<X, Y>(tauAct, fp.getReference()); 
		HmlOrFormula<X, Y> or1 = new HmlOrFormula<X, Y>(D1, arg);
		fp.setSubformula(or1);
		return fp;
	}
	
	private HmlFixPoint<X,Y>  getEmptyDiamond(){
		//min X.(<tau> X | <tau> arg )  = <tau>+ arg
		String name = "X";
		HmlFixPoint<X, Y> fp = new HmlFixPoint<X, Y>(false, name);
		HmlDiamondFormula<X, Y> D1 = new HmlDiamondFormula<X, Y>(tauAct, fp.getReference()); 
		HmlDiamondFormula<X, Y> D2 = new HmlDiamondFormula<X, Y>(tauAct, arg); 
		HmlOrFormula<X, Y> or1 = new HmlOrFormula<X, Y>(D1, D2);
		fp.setSubformula(or1);
		return fp;
	}
	
	private void createEquivalent(int type){
		switch (type) {
		case 0:
			//min X.(<tau> X | <tau> true )  = <tau>+ arg
			equivalent = getEmptyDiamond();	
			isEps = false;		
			break;
		case 1:
			// min X.( <filter> (min Y.(<tau> Y | arg)) | <tau> X )
			//min Y.(<tau> Y | arg)
			String name = "Y";
			HmlFixPoint<X, Y> tmp = new HmlFixPoint<X, Y>(false, name);
			HmlDiamondFormula<X, Y> D1 = new HmlDiamondFormula<X, Y>(tauAct, tmp.getReference()); 
			HmlOrFormula<X, Y> or1 = new HmlOrFormula<X, Y>(arg, D1);
			tmp.setSubformula(or1);
			//---------------------------------------
			String name2 = "X";
			equivalent =  new HmlFixPoint<X, Y>(false, name2);
			HmlDiamondFormula<X, Y> D2 = new HmlDiamondFormula<X, Y>(filter, tmp);
			HmlDiamondFormula<X, Y> D3 = new HmlDiamondFormula<X, Y>(tauAct, equivalent.getReference());
			HmlOrFormula<X, Y> or2 = new HmlOrFormula<X, Y>(D2, D3);
			equivalent.setSubformula(or2);	
			isEps = false;
			break;
		case 2:
			//min X.(<tau> X | arg)  = <tau>* arg
			equivalent = getEpsDiamond();		
			isEps = true;
			break;
		default:
			break;
		}
	}

	public boolean satisfies(X x ) {
		return equivalent.satisfies(x);
	}

	@Override
	public Proof<X, Y> getProof(X x) {
		return equivalent.getProof(x);
	}

	
	@Override
	protected HmlFormula<X, Y> doClone() { 
		return new HmlWeakDiamondFormula<X, Y>(filter, arg.clone(), tauAct);
	}

	@Override
	protected void doVisit(FormulaVisitor<X,Y> visitor) {
		visitor.visitWeakDiamond( this );
	}

	@Override
	protected String _toString() {
		String eps = isEps? "EPS": "";
		if (filter == null)
			return "<<>>"+arg.getUnicode();
		return "<<"+filter.toString()+">>"+arg;
	}

	@Override
	public String getUnicode() {
		if (filter == null)
			return "<<>>"+arg.getUnicode();
		return "<<"+filter.toString()+">>"+arg.getUnicode();
	}

	@Override
	public String operator() {
		if (filter == null || filter instanceof TauFilter) 
			return "WEAK-DIAMOND(TAU)";
		return "WEAK-DIAMOND( "+filter.toString()+" )";	
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlWeakDiamondFormula) {
			HmlWeakDiamondFormula<X, Y> f = (HmlWeakDiamondFormula<X, Y>) obj;
			
			if(!filter.equals(f.filter))
				return false;
			
			return arg.equals(f.arg);
			
		}		
		return false;
	}
}
