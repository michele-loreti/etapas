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
public final class HmlWeakBoxFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						extends HmlFormula<X,Y> {

	private Filter<Y> filter;
	private HmlFormula<X,Y> arg;
	private Y tauAct;
	private HmlFormula<X, Y> equivalent;
	private boolean isEps;
	
	public HmlWeakBoxFormula(HmlFormula<X,Y> arg, Y tauAct, boolean eps) {
		this.arg = arg;
		this.tauAct = tauAct;		 
		this.filter = new TauFilter<Y>();
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
	
	public HmlWeakBoxFormula(Filter<Y> f, HmlFormula<X,Y> arg, Y tauAct) {
		this.arg = arg;
		this.tauAct = tauAct;
		this.filter = f;
		int type = 1;		
		if(f instanceof TauFilter)
			type = 0;
		else if(f instanceof EpsFilter)
			type = 2;
		createEquivalent(type);
	}
	
	public HmlWeakBoxFormula(Y action , HmlFormula<X, Y> arg, Y tauAct) { 	
		this.arg = arg;
		this.tauAct = tauAct;
		
		int type = 1;
		if(action.isTau()){
			this.filter = new TauFilter<Y>();
			type = 0;
		} else{
			filter = new SingleActionFilter<Y>(action);
		}
		
		createEquivalent(type);
	}
	
	public HmlWeakBoxFormula(Set<Y> action_set, HmlFormula<X, Y> arg, Y tauAct) {
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
	
	private HmlFormula<X,Y>  getEmptyBox(){
		//max X.([tau]X & [tau] arg) 
		String name = "X";
		HmlFixPoint<X, Y> fp = new HmlFixPoint<X, Y>(true, name);
		HmlBoxFormula<X, Y> D1 = new HmlBoxFormula<X, Y>(tauAct, fp.getReference()); 
		HmlBoxFormula<X, Y> D2 = new HmlBoxFormula<X, Y>(tauAct, arg); 
		HmlAndFormula<X, Y> and = new HmlAndFormula<X, Y>(D1, D2);
		fp.setSubformula(and);

//		BoxFormula<X, Y> res = new BoxFormula<X, Y>(tauAct, fp);
		return fp;
	}
	
	private void createEquivalent(int type){
		if(type == 0){
			//max X.([tau]X & [tau] arg) = [[tau]]+ arg 
			equivalent = getEmptyBox();
			isEps = false;
		}else if(type == 1){
			// max X.( [filter] (max Y.(arg & [tau] Y)) & [tau] X )
			String name = "Y";
			HmlFixPoint<X, Y> tmp = new HmlFixPoint<X, Y>(true, name);
			HmlBoxFormula<X, Y> box1 = new HmlBoxFormula<X, Y>(tauAct, tmp.getReference()); 		
			HmlAndFormula<X, Y> and1 = new HmlAndFormula<X, Y>(arg, box1);
			tmp.setSubformula(and1);
			//---------------------------------------
			String name2 = "X";
			HmlFixPoint<X, Y> res = new HmlFixPoint<X, Y>(true, name2);
			HmlBoxFormula<X, Y> box2 = new HmlBoxFormula<X, Y>(filter, tmp);
			HmlBoxFormula<X, Y> box3 = new HmlBoxFormula<X, Y>(tauAct, res.getReference()); 
			HmlAndFormula<X, Y> and2 = new HmlAndFormula<X, Y>(box2, box3);
			res.setSubformula(and2);	
			equivalent = res;
			isEps = false;
		}else if(type == 2){
			//max X.([tau]X & arg) = [[tau]]* arg 
			equivalent = getEpsBox();
			isEps = true;
		}
	}
	
	private HmlFormula<X, Y> getEpsBox() {
		//max X.([tau]X & arg) = [tau]* arg 
		String name = "X";
		HmlFixPoint<X, Y> fp = new HmlFixPoint<X, Y>(true, name);
		HmlBoxFormula<X, Y> D1 = new HmlBoxFormula<X, Y>(tauAct, fp.getReference()); 
		HmlAndFormula<X, Y> and = new HmlAndFormula<X, Y>(D1, arg);
		fp.setSubformula(and);
		return fp;
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
		return new HmlWeakBoxFormula<X, Y>(filter,arg.clone(), tauAct);
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		visitor.visitWeakBox(this);
	}

	@Override
	protected String _toString() {
		if (filter == null)
			return "[[]]"+arg.getUnicode();
		return "[["+filter.toString()+"]]"+arg;
	}

	@Override
	public String getUnicode() {
		if (filter == null)
			return "[[]]"+arg.getUnicode();
		return "[["+filter.toString()+"]]"+arg.getUnicode();
	}

	@Override
	public String operator() {
		if (filter == null)
			return "WEAK-BOX(TAU)";
		return "WEAK-BOX( "+filter.toString()+" )";
	}

	@Override
	protected boolean _equals(Object obj) {
		if(obj == null)
			return false;
		
		if (obj instanceof HmlWeakBoxFormula) {
			HmlWeakBoxFormula<X, Y> f = (HmlWeakBoxFormula<X, Y>) obj;
			
			if(!filter.equals(f.filter))
				return false;
			
			return arg.equals(f.arg);
			
		}		
		return false;
	}
}
