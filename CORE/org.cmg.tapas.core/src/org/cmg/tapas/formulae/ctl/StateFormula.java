/**
 * 
 */
package org.cmg.tapas.formulae.ctl;

import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public interface StateFormula<S> {
	
	/**
	 * @author loreti
	 *
	 */
	public class Atomic<S> implements StateFormula<S> {
		
		private Filter<S> predicate;

		
		public Atomic( Filter<S> predicate ) {
			this.predicate = predicate;
		}
		
		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit(this);
		}
		
		public boolean eval( S s ) {
			return predicate.check( s );
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public class Next<S> implements StateFormula<S>{
		
		private boolean existential;
		
		private StateFormula<S> phi;
		
		public Next( boolean existential , StateFormula<S> phi ) {
			this.existential = existential;
			this.phi = phi;
		}
		

		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit(this);
		}
		
		public boolean isExistential() {
			return existential;
		}
		
		public StateFormula<S> getArgument() {
			return phi;
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public class Until<S> implements StateFormula<S> {
		
		private StateFormula<S> phi;
		private StateFormula<S> psi;
		private boolean existential;
		
		public Until( boolean existential , StateFormula<S> phi , StateFormula<S> psi ) {
			this.existential = existential;
			this.phi = phi;
			this.psi = psi;
		}

		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit(this);
		}
		
		public StateFormula<S> getLeftArgument() {
			return phi;
		}
		
		public StateFormula<S> getRightArgument() {
			return psi;
		}

		public boolean isExistential() {
			return existential;
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public class Not<S> implements StateFormula<S> {
		
		private StateFormula<S> argument;

		public Not( StateFormula<S> argument ) {
			this.argument = argument;
		}

		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit(this);
		}
		
		public StateFormula<S> getArgument() {
			return argument;
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public class Or<S> implements StateFormula<S> {

		private StateFormula<S> rightArgument;
		private StateFormula<S> leftArgument;

		public Or( StateFormula<S> leftArgument , StateFormula<S> rightArgument ) {
			this.leftArgument = leftArgument;
			this.rightArgument = rightArgument;
		}
		
		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}

		/**
		 * @return the rightArgument
		 */
		public StateFormula<S> getRightArgument() {
			return rightArgument;
		}

		/**
		 * @return the leftArgument
		 */
		public StateFormula<S> getLeftArgument() {
			return leftArgument;
		}
	

	}

	/**
	 * @author loreti
	 *
	 */
	public class And<S> implements StateFormula<S> {

		private StateFormula<S> rightArgument;
		private StateFormula<S> leftArgument;

		public And( StateFormula<S> leftArgument , StateFormula<S> rightArgument ) {
			this.leftArgument = leftArgument;
			this.rightArgument = rightArgument;
		}
		
		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}

		/**
		 * @return the rightArgument
		 */
		public StateFormula<S> getRightArgument() {
			return rightArgument;
		}

		/**
		 * @return the leftArgument
		 */
		public StateFormula<S> getLeftArgument() {
			return leftArgument;
		}
	
	}

	public <T> T accept( CtlVisitor<S,T> visitor );
	
	public static class True<S> implements StateFormula<S> {

		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
	}
	
	public static class False<S> implements StateFormula<S> {

		@Override
		public <T> T accept(CtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
	}

}
