package org.cmg.tapas.core.graph.algorithms;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * FastTreeSet d� una implementazione di SortedSet
 * che ottimizza equals(FastTreeSet), hashCode()  ed offre 
 * <code>bigger(fastSet1,fastSet2)</code>.
 * <p>
 * Assume che per la specifica classe di tipo E sia
 * correttamente implementato il metodo <tt>hashCode</tt>,
 * ossia che due oggetti di tale classe che risultano uguali
 * abbiano lo stesso hashCode.
 * <p>
 * Eventuali classi derivate da questa devono
 * chiamare super.add(E) e super.remove(E)
 * in eventuali overridding di add(E) e remove(E).
 *
 * @param <E> tipo degli elementi.
 * 
 * @author Guzman Tierno
 */
public class FastTreeSet<E extends Comparable<? super E>> extends TreeSet<E> 
implements Comparable<FastTreeSet<E>> {			
	private static final long serialVersionUID = 1;
	
    /** Cache the hash code for the set. */
    private int hash; 							// default hash to 0
    
    /** Costruisce un FastTreeSet vuoto. **/
    public FastTreeSet() {   
        // empty set
    }
    
    /** Costruisce un FastTreeSet a partire da un Set<E>. **/
    public FastTreeSet(Collection<? extends E> set) {
    	super(set);								// calls add indirectly
    }

	/**
	 * Returns a hash code for this set. The hash code for a
	 * <code>FastTreeSet</code> object is computed as
	 * <blockquote><pre>
	 * 		e_0.hashCode() + ... + e_n.hashCode()
	 * </pre></blockquote>
	 * using <code>int</code> arithmetic, where the <code>e_i</code>'s
	 * are the elements of the set and <code>n</code> is the size of
	 * the set.
	 * (The hash value of the empty set is zero.)
	 * The method assumes that E implements hashCode() correctly
	 * so that two of objects of type E that are equal have the
	 * same hashCode.
	 *
	 * @return  a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return hash;							// hash
	}

	/** Adds the specified element to this set if it is not already present. **/ 
	@Override
	public boolean add(E e) {
		if( super.add(e) ) {
			hash += e.hashCode();				// hash add
			return true;
		}
		return false;	
	}

	/** Removes the specified element from this set if it is present. **/
	@Override
	public boolean remove(Object o) {
		if( super.remove(o) ) {
			hash -= o.hashCode();				// hash remove
			return true;
		}
		return false;
	}

	/** Removes all of the elements from this set. **/
	@Override
	public void clear() {
		super.clear();
		hash = 0;								// hash clear
	}
	
	/** 
	 * Rende true se l'insieme specificato contiene gli stessi elementi
	 * di questo. 
	 **/
	public boolean equals(FastTreeSet<E> other) {
		// try using hash or size
		if( other==null || hash!=other.hash || size()!=other.size() )
			return false;						// hash
		
		// use fast comparison
		return compareTo( other )==0;
	}
	
	/** Rende un iteratore per questo insieme. **/
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Iterator<E> iter = FastTreeSet.super.iterator();
			E current;
			public boolean hasNext() {
				return iter.hasNext();
			}
			public E next() {				
				return current = iter.next();
			}
			public void remove() {
				hash -= current.hashCode();
				iter.remove();	// does not call Set.remove()
			}
		};
	}
	
	
	/**
	 * Ordina per dimensioni decrescenti e, a parit�,
	 * in ordine alfabetico.
	 **/
	public int compareTo(FastTreeSet<E> other) {
		// different sizes
		if( other.size() - this.size() != 0 )
			return other.size() - this.size();
			
		// same size
		Iterator<E> iter1 = this.iterator();
		Iterator<E> iter2 = other.iterator();		
		E e1;
		E e2;
		int compare = 0;
		while( compare==0 ) {
			if( !iter1.hasNext() )
				return 0;			
			e1 = iter1.next();
			e2 = iter2.next();
			compare = e1.compareTo(e2);
		}		
		
		return compare;		
	}

//	/**
//	 * Confronta i due insiemi specificati e rende l'indice 
//	 * del pi� grande. <p>
//	 * Rende 1 se <tt>set1</tt> contiene <tt>set2</tt>. <p>
//	 * Rende 2 se <tt>set2</tt> contiene <tt>set1</tt>. <p>
//	 * Rende 0 se <tt>set1</tt> � uguale a <tt>set2</tt>. <p>
//	 * Rende -1 altrimenti. <p>
//	 * Implementazione base.
//	 **/
//	@SuppressWarnings("unused")
//	private static <E extends Comparable<? super E>> int bigger0(
//		SortedSet<? extends E> set1, 
//		SortedSet<? extends E> set2
//	) {
//		if( set1.containsAll(set2) ) {
//			if( set1.size()==set2.size() )
//				return 0; 
//			return 1;
//		}
//		
//		if( set2.containsAll(set1) )
//			return 2;
//			
//		return -1;
//	}
	
	/**
	 * Confronta i due insiemi specificati e rende l'indice 
	 * del pi� grande. <p>
	 * Rende 1 se <tt>set1</tt> contiene <tt>set2</tt>. <p>
	 * Rende 2 se <tt>set2</tt> contiene <tt>set1</tt>. <p>
	 * Rende 0 se <tt>set1</tt> � uguale a <tt>set2</tt>. <p>
	 * Rende -1 altrimenti. <p>
	 **/
	public static <E extends Comparable<? super E>> int bigger(
		SortedSet<? extends E> set1, 
		SortedSet<? extends E> set2
	) {
		Iterator<? extends E> waiting = set1.iterator();
		Iterator<? extends E> going = set2.iterator();
		Iterator<? extends E> temp;		
		E g = null;					// element going
		E w = null;					// element waiting
		E t;						// temp element for swapping
		boolean skipped = false;	// some elements of going have been skipped
		boolean swapped = false;	// swapped sets flag
		int compare = 0;			// g.compareTo(w)		
				
		boolean stop = false;
		while( !stop ) {
			while( compare==0 ) {	// same value: proceed together
				if( !going.hasNext() || !waiting.hasNext() ) {					
					stop = true;
					break;
				}
				g = going.next();
				w = waiting.next();
				compare = g.compareTo(w);
			}
			while( compare<0 ) {	// g is lesser: make it proceed
				skipped = true;
				if( !going.hasNext() ) {
					stop = true;
					break;
				}
				g = going.next();
				compare = g.compareTo(w);
			}
			if( compare>0 ) {		// w is lesser: swap if possible
				if( skipped ) 
					return -1;						
				temp = going; going = waiting; waiting = temp;	// swap
				t = g; g = w; w = t;							// swap
				swapped = true;
				compare *= -1;								
			}
		}
		
		if( !going.hasNext() ) {
			if( !waiting.hasNext() && compare==0 )
				return !skipped ? 0 : swapped ? 1 : 2;
			return skipped ? -1 : swapped ? 2 : 1;
		}
		
		return swapped ? 1 : 2;		
	}


}

