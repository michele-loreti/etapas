package org.cmg.tapas.formulae.ltl;

import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;

public abstract class LtlFormula<S> implements Comparable<LtlFormula<?>>{
	
	public abstract LtlFormula<S> simplyNot();
	public abstract int getSize();
	public abstract int computeElementaryExtension( Set<LtlFormula<S>> set );
	
	public abstract <T> T acceptVisitor(LtlVisitor<S, T> visitor);
	
	public Filter<S> getFilter(){
		return null;
	}; 
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public int compareTo(LtlFormula<?> o) {
		int toReturn = this.getSize() - o.getSize();
		if( toReturn == 0 ){
			return this.toString().compareTo(o.toString());
		}
		return toReturn;
	}

	public LtlFormula<S> getNegation() {
		return new Not<S>( this );
	}
		
	public static class Atomic<S> extends LtlFormula<S>{
		
		private String name;
		private Filter<S> predicate;
		
		public Atomic( String name , Filter<S> predicate ) {
			this.name = name;
			this.predicate = predicate;
		}
		
		public Filter<S> getFilter(){
			return predicate;
		}
		
		public boolean eval( S s) {
			return predicate.check( s );
		}

		@Override
		public LtlFormula<S> simplyNot() {
			return this;
		}

		public String getName() {
			return name;
		}


		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Atomic<?>) {
				return name.equals(((Atomic<?>) obj).name);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}

		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			return 0;
		}
		
	}
	
	public static class Next<S> extends LtlFormula<S> { 
		
		private LtlFormula<S> argument;
		
		public Next( LtlFormula<S> phi ) {
			this.argument = phi;
		}
		
		public LtlFormula<S> getArgument() {
			return argument;
		}
	
		@Override
		public LtlFormula<S> simplyNot() {
			return new Next<S>( argument.simplyNot() );
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Next<?>) {
				Next<?> next = (Next<?>) obj;
				return argument.equals(next.argument);
			}
			return false;
		}

		@Override
		public String toString() {
			return "X("+argument+")";
		}

		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
		@Override
		public int getSize() {
			return 1 + argument.getSize();
		}
		
		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			return 0;
		}

	}
	
	public static class Until<S> extends LtlFormula<S> {
		
		private LtlFormula<S> leftArgument;
		private LtlFormula<S> rightArgument;
		
		public Until( LtlFormula<S> phi, LtlFormula<S> psi){
			this.leftArgument = phi;
			this.rightArgument = psi;
		}
		
		public LtlFormula<S> getLeftArgument() {
			return leftArgument;
		}
		
		public LtlFormula<S> getRightArgument() {
			return rightArgument;
		}

		@Override
		public LtlFormula<S> simplyNot() {
			return new Until<S>(leftArgument.simplyNot() , rightArgument.simplyNot() );
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Until<?>) {
				Until<?> until = (Until<?>) obj;
				return leftArgument.equals(until.leftArgument) && rightArgument.equals(until.rightArgument);
			}
			return false;
		}

		@Override
		public String toString() {
			return "("+leftArgument+")U("+rightArgument+")";
		}
		
		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}

		@Override
		public int getSize() {
			return 1 + leftArgument.getSize() + rightArgument.getSize();
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			if (set.contains(rightArgument)) {
				return 1;
			}
			if (set.contains(leftArgument)) {
				return 0;
			}
			return -1;
 		}

	}
	
	public static class Not<S> extends LtlFormula<S> {
		
		private LtlFormula<S> argument;
		
		public Not( LtlFormula<S> argument ) {
			this.argument = argument;
		}
		
		public LtlFormula<S> getArgument() {
			return argument;
		}
		
		@Override
		public LtlFormula<S> simplyNot() {
			LtlFormula<S> simplified = argument.simplyNot();
			if (simplified instanceof Not) {
				return ((Not<S>) simplified).argument;
			}
			return new Not<S>(simplified);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Not<?>) {
				Not<?> not = (Not<?>) obj;
				return argument.equals(not.argument);
			}
			return false;
		}

		@Override
		public String toString() {
			return "!("+argument+")";
		}

		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
		@Override
		public LtlFormula<S> getNegation() {
			return argument;
		}

		@Override
		public int getSize() {
			return 1 + argument.getSize();
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			return -computeElementaryExtension(set);
		}

		@Override
		public Filter<S> getFilter() {
			Filter<S> toReturn = null;
			if(argument instanceof LtlFormula.Atomic){
					toReturn = new NotFilter<S>(argument.getFilter());
			}
			
			return toReturn;
		}
		
	}
	
	public static class Or<S> extends LtlFormula<S> {
		
		private LtlFormula<S> rightArgument;
		private LtlFormula<S> leftArgument;
		
		public Or( LtlFormula<S> leftArgument , LtlFormula<S> rightArgument ) {
			this.leftArgument = leftArgument;
			this.rightArgument = rightArgument;
		}
		
		/**
		 * @return the rightArgument
		 */
		public LtlFormula<S> getRightArgument() {
			return rightArgument;
		}

		/**
		 * @return the leftArgument
		 */
		public LtlFormula<S> getLeftArgument() {
			return leftArgument;
		}
		
		@Override
		public LtlFormula<S> simplyNot() {
			return new Or<S>(leftArgument.simplyNot() , rightArgument.simplyNot() );
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Or<?>) {
				Or<?> other = (Or<?>) obj;
				return leftArgument.equals(other.leftArgument) && rightArgument.equals(other.rightArgument);
			}
			return false;
		}

		@Override
		public String toString() {
			return "("+leftArgument+")||("+rightArgument+")";
		}

		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}

		@Override
		public int getSize() {
			return 1 + leftArgument.getSize() + rightArgument.getSize();
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			if (set.contains(leftArgument)||set.contains(rightArgument)) {
				return 1;
			} else {
				return -1;
			}
		}
	}
	
	public static class And<S> extends LtlFormula<S> {

		private LtlFormula<S> rightArgument;
		private LtlFormula<S> leftArgument;

		public And( LtlFormula<S> leftArgument , LtlFormula<S> rightArgument ) {
			this.leftArgument = leftArgument;
			this.rightArgument = rightArgument;
		}
		
		/**
		 * @return the rightArgument
		 */
		public LtlFormula<S> getRightArgument() {
			return rightArgument;
		}

		/**
		 * @return the leftArgument
		 */
		public LtlFormula<S> getLeftArgument() {
			return leftArgument;
		}

		@Override
		public LtlFormula<S> simplyNot() {
			return new And<S>(leftArgument.simplyNot() , rightArgument.simplyNot() );
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof And<?>) {
				And<?> other = (And<?>) obj;
				return leftArgument.equals(other.leftArgument) && rightArgument.equals(other.rightArgument);
			}
			return false;
		}

		@Override
		public String toString() {
			return "("+leftArgument+")&&("+rightArgument+")";
		}
	
		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
		@Override
		public int getSize() {
			return 1 + leftArgument.getSize() + rightArgument.getSize();
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			if (set.contains(leftArgument)&&set.contains(rightArgument)) {
				return 1;
			} else {
				return -1;
			}
		}
		
	}
	
	public static class True<S> extends LtlFormula<S> {

		@Override
		public LtlFormula<S> simplyNot() {
			return this;
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof True<?>);
		}

		@Override
		public String toString() {
			return "true";
		}
		
		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
		@Override
		public int getSize() {
			return 1;
		}

		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			return 1;
		}
		
	}
	
	public static class False<S> extends LtlFormula<S> {

		@Override
		public LtlFormula<S> simplyNot() {
			return this;
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof False<?>);
		}

		@Override
		public String toString() {
			return "false";
		}
			
		@Override
		public <T> T acceptVisitor(LtlVisitor<S, T> visitor) {
			return visitor.visit( this );
		}
		
		@Override
		public int getSize() {
			return 1;
		}


		@Override
		public int computeElementaryExtension(Set<LtlFormula<S>> set) {
			return -1;
		}

	}
}

