/**
 * 
 */
package org.cmg.tapas.mc.actl.global.enf;

/**
 * @author loreti
 *
 */
public interface ENFActlVisitor<X, Y, Z> {
	
	public Z visit( ENFActlFormula.True<X, Y> f );
	public Z visit( ENFActlFormula.And<X, Y> f );
	public Z visit( ENFActlFormula.Not<X, Y> f );
	public Z visit( ENFActlFormula.AtomicPredicate<X, Y> f );
	public Z visit( ENFActlFormula.ExistsNext<X,Y> f);
	public Z visit( ENFActlFormula.ExistsUntil<X,Y> f);
	public Z visit( ENFActlFormula.ExistsAlways<X,Y> f);

}
