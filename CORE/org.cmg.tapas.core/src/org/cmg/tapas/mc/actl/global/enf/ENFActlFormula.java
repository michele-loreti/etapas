/**
 * 
 */
package org.cmg.tapas.mc.actl.global.enf;

import org.cmg.tapas.core.graph.filter.Filter;


/**
 * @author loreti
 *
 */
public abstract class ENFActlFormula<X,Y> {

	public abstract <Z> Z accept( ENFActlVisitor<X,Y,Z> visitor );

	/**
	 * @author loreti
	 *
	 */
	public static class ExistsAlways<X, Y> extends ENFActlFormula<X, Y> {

		private Filter<Y> labelPredicate;
		private ENFActlFormula<X,Y> alwaysFormula;

		/**
		 * @param labelPredicate
		 * @param alwaysFormula
		 */
		public ExistsAlways(Filter<Y> labelPredicate,
				ENFActlFormula<X, Y> alwaysFormula) {
			super();
			this.labelPredicate = labelPredicate;
			this.alwaysFormula = alwaysFormula;
		}

		/**
		 * @return the labelPredicate
		 */
		public Filter<Y> getLabelPredicate() {
			return labelPredicate;
		}

		/**
		 * @return the alwaysFormula
		 */
		public ENFActlFormula<X, Y> getAlwaysFormula() {
			return alwaysFormula;
		}

		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y, Z> visitor) {
			return visitor.visit(this);
		}
		
	}

	/**
	 * @author loreti
	 *
	 */
	public static class ExistsUntil<X, Y> extends ENFActlFormula<X, Y>{
		
		private ENFActlFormula<X, Y> leftArgument;
		private Filter<Y> labelPredicate;
		private ENFActlFormula<X, Y> rightArgument;
		
		/**
		 * @param leftArgument
		 * @param labelPredicate
		 * @param rightArgument
		 */
		public ExistsUntil(ENFActlFormula<X, Y> leftArgument,
				Filter<Y> labelPredicate, ENFActlFormula<X, Y> rightArgument) {
			super();
			this.leftArgument = leftArgument;
			this.labelPredicate = labelPredicate;
			this.rightArgument = rightArgument;
		}

		/**
		 * @return the leftArgument
		 */
		public ENFActlFormula<X, Y> getLeftArgument() {
			return leftArgument;
		}

		/**
		 * @return the labelPredicate
		 */
		public Filter<Y> getLabelPredicate() {
			return labelPredicate;
		}

		/**
		 * @return the rightArgument
		 */
		public ENFActlFormula<X, Y> getRightArgument() {
			return rightArgument;
		}

		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y, Z> visitor) {
			return visitor.visit(this);
		}
		
		

	}

	/**
	 * @author loreti
	 *
	 */
	public static class ExistsNext<X, Y> extends ENFActlFormula<X, Y>{
		
		private Filter<Y> labelPredicate;
		private ENFActlFormula<X,Y> stateFormula;
		
		public ExistsNext( Filter<Y> labelPredicate , ENFActlFormula<X,Y> stateFormula ) {
			this.labelPredicate = labelPredicate;
			this.stateFormula = stateFormula;
		}

		public Filter<Y> getLabelPredicate() {
			return labelPredicate;
		}
		
		public ENFActlFormula<X,Y> getStateFormula() {
			return stateFormula;
		}
		
		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y,Z> visitor) {
			return visitor.visit(this);
		}
		
	}

	/**
	 * @author loreti
	 *
	 */
	public static class AtomicPredicate<X, Y> extends ENFActlFormula<X, Y>{

		private Filter<X> predicate;
		
		public AtomicPredicate( Filter<X> predicate ) {
			this.predicate = predicate;
		}
		
		public Filter<X> getPredicate() {
			return predicate;
		}
		
		public boolean eval( X s ) {
			return predicate.check(s);
		}
		
		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y, Z> visitor) {
			return visitor.visit(this);
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public static class Not<X, Y> extends ENFActlFormula<X,Y> {

		private ENFActlFormula<X, Y> argument;
		
		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y, Z> visitor) {
			return visitor.visit(this);
		}

		/**
		 * @param argument
		 */
		public Not(ENFActlFormula<X, Y> argument) {
			super();
			this.argument = argument;
		}

		/**
		 * @return the argument
		 */
		public ENFActlFormula<X, Y> getArgument() {
			return argument;
		}

	}
	
	/**
	 * @author loreti
	 *
	 */
	public static class And<X, Y> extends ENFActlFormula<X, Y> {

		private ENFActlFormula<X, Y> left;
		private ENFActlFormula<X, Y> right;
		
		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y, Z> visitor) {
			return visitor.visit(this);
		}

		/**
		 * @param left
		 * @param right
		 */
		public And(ENFActlFormula<X, Y> left, ENFActlFormula<X, Y> right) {
			super();
			this.left = left;
			this.right = right;
		}

		/**
		 * @return the left
		 */
		public ENFActlFormula<X, Y> getLeft() {
			return left;
		}

		/**
		 * @return the right
		 */
		public ENFActlFormula<X, Y> getRight() {
			return right;
		}

	}

	/**
	 * @author loreti
	 *
	 */
	public static class True<X,Y> extends ENFActlFormula<X, Y> {

		@Override
		public <Z> Z accept(ENFActlVisitor<X, Y,Z> visitor) {
			return visitor.visit(this);
		}				
		
	}
	
}
