/**
 * 
 */
package org.cmg.tapas.formulae.ltl;

/**
 * @author loreti
 *
 */
public interface LtlVisitor<S,T> {
	
	public T visit( LtlFormula.True<S> f );

	public T visit( LtlFormula.False<S> f );

	public T visit( LtlFormula.And<S> f );

	public T visit( LtlFormula.Or<S> f );

	public T visit( LtlFormula.Not<S> f );

	public T visit( LtlFormula.Until<S> f );

	public T visit( LtlFormula.Next<S> f );

	public T visit( LtlFormula.Atomic<S> f );

}
