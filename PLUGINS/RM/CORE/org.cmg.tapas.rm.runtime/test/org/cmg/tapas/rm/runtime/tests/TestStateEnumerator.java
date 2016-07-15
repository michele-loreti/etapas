package org.cmg.tapas.rm.runtime.tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.cmg.tapas.rm.runtime.StateEnumerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestStateEnumerator {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrimesRange() {
		int[] bounds = new int[] { 2 , 3 , 5 , 7 , 9 };
		StateEnumerator se = StateEnumerator.createEnumerator(bounds);
		int maxStateHash = getMaxStateHash( bounds );
		assertArrayEquals(new int[] {1, 2,6,30,210} , se.getArrayB() );
		assertEquals(maxStateHash-1, se.enumerate(new int[] {1,2,4,6,8}));
		for( int i=0 ; i<maxStateHash ; i++ ) {
			int[] state = se.hashToArray( i );
			assertEquals( "Hashing of "+Arrays.toString(state) , i , se.enumerate(state) );
		}
	}

	@Test
	public void testMultipleRange() {
		int[] bounds = new int[] { 2 , 4 , 8 , 16 , 32 };
		StateEnumerator se = StateEnumerator.createEnumerator(bounds);
		int maxStateHash = getMaxStateHash( bounds );
		System.out.println(maxStateHash);
		assertArrayEquals(new int[] {1, 2, 8 , 64 , 1024 } , se.getArrayB() );
		assertEquals(maxStateHash-1, se.enumerate(new int[] {1,3,7,15,31}));
		for( int i=0 ; i<maxStateHash ; i++ ) {
			int[] state = se.hashToArray( i );
			assertEquals( "Hashing of "+Arrays.toString(state) , i , se.enumerate(state) );
		}
	}

	@Test
	public void testMultiplicity() {
		int[] bounds = new int[] { 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 , 4 };
		StateEnumerator se = StateEnumerator.createEnumerator(bounds);
		int maxStateHash = getMaxStateHash( bounds );
		System.out.println(maxStateHash);
		for( int i=0 ; i<maxStateHash ; i++ ) {
			int[] state = se.hashToArray( i );
			assertEquals( "Hashing of "+Arrays.toString(state) , i , se.enumerate(state) );
		}
	}

	private int getMaxStateHash(int[] bounds) {
		int max = 1;
		for( int i=0 ; i<bounds.length ; i++ ) {
			max = max*bounds[i];
		}
		return max;
	}

	
	
}
