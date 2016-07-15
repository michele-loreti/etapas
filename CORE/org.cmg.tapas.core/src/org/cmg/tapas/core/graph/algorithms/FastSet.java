package org.cmg.tapas.core.graph.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * FastSet d� una implementazione di Set
 * che ottimizza add, remove, contains, equals(FastSet), hashCode().
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
public class FastSet<E> extends HashSet<E> implements Set<E> {			
	private static final long serialVersionUID = 1;

    /** Cache the hash code for the set. */
    private int hash; 							// default hash to 0
    
    /** Costruisce un FastSet vuoto. **/
    public FastSet() {    	
        // empty set
    }
    
    /** Costruisce un FastSet a partire da un Set<E>. **/
    public FastSet(Collection<? extends E> set) {
    	super(set);								// calls add indirectly
    }

	/**
	 * Returns a hash code for this set. The hash code for a
	 * <code>FastSet</code> object is computed as
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
	public boolean equals(FastSet<E> other) {
		// try using hash or size
		if( other==null || hash!=other.hash || size()!=other.size() )
			return false;						// hash
		
		// use fast comparison
		return super.equals( other );
	}
	
	/** Rende un iteratore per questo insieme. **/
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			Iterator<E> iter = FastSet.super.iterator();
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
	
}

