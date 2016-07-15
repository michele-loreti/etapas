/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author loreti
 *
 */
public interface FormulaVisitor<X extends Process<X, Y>,Y extends ActionInterface> {
	
	void visitDiamond( HmlDiamondFormula<X,Y> f);
	void visitWeakDiamond( HmlWeakDiamondFormula<X,Y> f);
	void visitBox( HmlBoxFormula<X, Y> name);
	void visitWeakBox( HmlWeakBoxFormula<X, Y> name);
	void visitAnd( HmlAndFormula<X, Y> f );
	void visitDiv(HmlDivergenceFormula<X, Y> f );
	void visitOr( HmlOrFormula<X,Y> f);
	void visitNot( HmlNotFormula<X, Y> f );
	void visitTrue( HmlTrue<X,Y> f);
	void visitFalse( HmlFalse<X,Y> f);
	void visitFixPoint( HmlFixPoint<X, Y> f );
	void visitReference( HmlFixPoint<X,Y>.Reference ref );
	void visitUndefined( Undefined<X, Y> f);
	void visitAtomic( HmlAtomic<X, Y> f );
	
}
