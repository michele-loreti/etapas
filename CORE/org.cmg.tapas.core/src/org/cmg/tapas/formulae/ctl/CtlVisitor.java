package org.cmg.tapas.formulae.ctl;

public interface CtlVisitor<S, R> {

	public R visit( StateFormula.True<S> f );

	public R visit( StateFormula.False<S> f );
	
	public R visit( StateFormula.And<S> f );
	
	public R visit( StateFormula.Or<S> f );

	public R visit( StateFormula.Not<S> f );
	
	public R visit( StateFormula.Until<S> f );
	
	public R visit( StateFormula.Next<S> f );
	
	public R visit( StateFormula.Atomic<S> f );
		
}
