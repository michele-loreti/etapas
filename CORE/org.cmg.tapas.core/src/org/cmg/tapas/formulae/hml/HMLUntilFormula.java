package org.cmg.tapas.formulae.hml;


import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;

/**
 * @author calzolai
 *
 */
public class HMLUntilFormula<X extends Process<X, Y>,Y extends ActionInterface> 
			extends HmlFormula<X,Y> {
	
	private static final String NAME = "X";
	public static final int MIN = 0;
	public static final int COMPACT = 0;	
	
	private HmlFormula<X, Y> formula1;
	private Y act;
	private HmlFormula<X, Y> formula2;
	
	private HmlFixPoint<X, Y> equivalent;
	private int toStringFormat;
	
	public HMLUntilFormula(Y a){
		this(new HmlTrue<X, Y>(), a, new HmlTrue<X, Y>());
	}
	
	public HMLUntilFormula(HmlFormula<X, Y> f1, Y a, HmlFormula<X, Y> f2){
		//min Z.( formula1 and
		//		  ((<act> formula2) or <tau> Z)					
		//		)
		toStringFormat = COMPACT;
		formula1 = f1;
		act = a;
		formula2 = f2;		
		
		String name = getFreshName();
		equivalent = new HmlFixPoint<X, Y>(false, name);

		//<act> formula2
		HmlDiamondFormula<X, Y> tmp1 = new HmlDiamondFormula<X, Y>(act, formula2);
		
		//<tau> Z
		HmlFormula<X,Y> ref = equivalent.getReference();
		TauFilter<Y> filter = new TauFilter<Y>();
		HmlDiamondFormula<X, Y> tmp2 = new HmlDiamondFormula<X, Y>(filter, ref);
		
		//((<act> formula2) or <tau> Z)	
		HmlOrFormula<X, Y> or = new HmlOrFormula<X, Y>(tmp1, tmp2);
		
		//formula1 and ((<act> formula2) or <tau> Z)	
		HmlAndFormula<X, Y> and = new HmlAndFormula<X, Y>(formula1, or);
		
		equivalent.setSubformula(and);
	}
	
	private String getFreshName(){
		int count = 1;
		String res = NAME+count;
		String f1 = formula1.toString();
		String f2 = formula2.toString();
		while(f1.contains(res) | f2.contains(res)){
			res = NAME+count++;
		}		
		return res;
	}

	@Override
	protected boolean _equals(Object o) {
		if(o != null){
			if (o instanceof HMLUntilFormula) {
				HMLUntilFormula<X, Y> f = (HMLUntilFormula<X, Y>) o; 
				return equivalent.equals(f.equivalent);
			}
		}

		return false;
	}

	@Override
	protected String _toString() {
		String res = "(";
		switch (toStringFormat) {
		case MIN:
			res += formula1.toString()
					+" <"+act.toString()+"> "
					+ formula2.toString();
			break;

		default:
			res += equivalent.toString();
			break;
		}
		return res+")";
	}

	@Override
	protected HmlFormula<X, Y> doClone() {
		HmlFormula<X, Y> f1 = formula1.clone();
		HmlFormula<X, Y> f2 = formula2.clone();
		HMLUntilFormula<X, Y> res = new HMLUntilFormula<X, Y>(f1, act, f2);
		return res;
	}

	@Override
	protected void doVisit(FormulaVisitor<X, Y> visitor) {
		equivalent.doVisit(visitor);
	}

	@Override
	public Proof<X, Y> getProof(X s) {
		return equivalent.getProof(s);
	}

	@Override
	public String getUnicode() {
		String res = "(";
		switch (toStringFormat) {
		case MIN:
			res += formula1.getUnicode()
					+" <"+act.toString()+"> "
					+ formula2.getUnicode();
			break;

		default:
			res = equivalent.getUnicode();
			break;
		}
		return res+")";
	}

	@Override
	public String operator() {
		return "";
	}

	@Override
	public boolean satisfies(X s) {
		return equivalent.satisfies(s);
	}
	
	public void setStringFormat(int format){
		if(format == COMPACT || format == MIN)
			toStringFormat = format;
	}
	
	public int getStringFormat(){
		return toStringFormat;
	}

}
