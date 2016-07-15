package org.cmg.tapas.formulae.actl;


import org.cmg.tapas.formulae.actl.state.ActlAnd;
import org.cmg.tapas.formulae.actl.state.ActlFalse;
import org.cmg.tapas.formulae.actl.state.ActlNot;
import org.cmg.tapas.formulae.actl.state.ActlOr;
import org.cmg.tapas.formulae.actl.state.ActlQuantification;
import org.cmg.tapas.formulae.actl.state.ActlTrue;


public interface ActlFormulaVisitor<X,Y> {
	
	//Path formulae
//	public void visitNextFormula( NextFormula<X, Y> f );
//	public void visitUntilFormula( UntilFormula<X, Y> f );
	
	//State formulae
	public void visitNotFormula( ActlNot<X, Y> f );
	public void visitAndFormula( ActlAnd<X, Y> f );
	public void visitOrFormula( ActlOr<X,Y> f);	
	public void visitTrue( ActlTrue<X,Y> f);
	public void visitFalse( ActlFalse<X,Y> f);		
	public void visitQuantificationFormula( ActlQuantification<X, Y> f);
}
