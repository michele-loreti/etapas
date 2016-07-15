/**
 * 
 */
package org.cmg.tapas.rm.runtime.tests;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.tapas.rm.runtime.State;

/**
 * @author loreti
 *
 */
public class Test {
	
	private ArrayList<Integer> elements = new ArrayList<Integer>();
	
	
	public void set( int i , Integer v ) {
		for( int j = elements.size() ; j<=i ; j++ ) {
			elements.add(null);
		}
		elements.set(i, v);
	}
	
	public Integer get( int i ) {
		if ( i<elements.size() ) {
			return elements.get(i);
		} else {
			return null;
		}
	}
	
	public static void main( String[] argv ) {
		Test t = new Test();
		t.set(10, 10);
		System.out.println( t.get(10) );
	}

}
