/**
 * 
 */
package org.cmg.tapas.rm.runtime;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author loreti
 *
 */
public class StateEnumerator {
	
	private int[] vectorB;
	private int[] vectorK;
	private int[] minValues;
	
	private StateEnumerator( int[] vectorB , int[] vectorK , int[] minValues) {
		this.vectorB = vectorB;
		this.vectorK = vectorK;
		this.minValues = minValues;
	}
	
	public int get( int v , int i ) {
		return minValues[i]+_get( v , i );
	}
	
	public int get( State state , int i ) {
		return get( state.state , i );
	}
	
	private int _get( int v , int i ) {
		return (v/vectorB[i])%vectorK[i];
	}
	
	public int set( int v , int i , int x ) {
		int old = _get(v , i)*vectorB[i];
		return  (v-old)+_element( i , x );
	}
	
	private int _element( int i , int x ) {
		return ((x-minValues[i])*vectorB[i]);
	}
	
	public int enumerate( int ... values  ) {
		int v = 0;
		for( int i=0 ; i<values.length ; i++ ) {
			v += _element( i , values[i] );
		}
		return v;
	}
	
	public static StateEnumerator createEnumerator( int[] ranges ) {
		int[] vectorB = new int[ranges.length];
		int[] minValues = new int[ranges.length];
		int foo = 1;
		for( int i=0 ; i<ranges.length ; i++ ) {
			vectorB[i] = foo;
			foo = ranges[i]*foo;
		}
		return new StateEnumerator(vectorB, ranges, minValues);
	}
	
	public static StateEnumerator createEnumerator( int[] lowerBounds , int[] upperBounds ) {
		if (lowerBounds.length != upperBounds.length) {
			throw new IllegalArgumentException();
		}
		int[] vectorB = new int[lowerBounds.length];
		int[] ranges = new int[lowerBounds.length];
		int foo = 1;
		for( int i=0 ; i<lowerBounds.length ; i++ ) {
			vectorB[i] = foo;
			ranges[i] = upperBounds[i] - lowerBounds[i] + 1;
			foo = ranges[i]*foo;
		}
		
		return new StateEnumerator(vectorB, ranges, lowerBounds);
		
	}
	
	public String stringOfState(int state) {
		return Arrays.toString(hashToArray(state));
	}

	public int[] getArrayB() {
		return vectorB;
	}

	public int[] hashToArray(int state) {
		int[] values = new int[vectorK.length];
		for( int i=0 ; i<vectorK.length ; i++ ) {
			values[i] = get( state , i );
		}
		return values;
	}

	public int size() {
		int size = 1;
		for( int i=0 ; i< vectorK.length ; i++ ) {
			size *= vectorK[i];
		}
		return size;
	}

	public Iterable<State> states(final AbstractReactiveModule module) {
		return new Iterable<State>() {
			
			@Override
			public Iterator<State> iterator() {
				return new Iterator<State>(){
					
					private int current;
					private int max = size();

					@Override
					public boolean hasNext() {
						return current<max;
					}

					@Override
					public State next() {
						State toReturn = null;
						if (current < max) {
							toReturn = new State(module, current);
							current++;
						}
						return toReturn;
					}

					@Override
					public void remove() {
					}
					
				};
			}
		};
	}

}
