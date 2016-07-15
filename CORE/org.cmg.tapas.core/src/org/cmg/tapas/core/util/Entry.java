package org.cmg.tapas.core.util;

import java.util.Map;

/** 
 * Classe usata per rendere valori che sono coppie.
 * Si � preferito implementare Map.Entry piuttosto che  
 * creare una classe ad hoc perch� Map.Entry era gi� usata
 * in vari punti.
 **/
public class Entry<A1,S1> implements Map.Entry<A1,S1> {
	private A1 key;
	private S1 value;
	
	public Entry(A1 key, S1 value) {
		this.key = key;
		this.value = value;
	}
	/** Rende la chiave di questa entry **/
	public A1 getKey() {
		return key;
	}
    /** Rende il valore di questa entry **/
	public S1 getValue() {
		return value;
	}
    /** Non supportato. **/
	public S1 setValue(S1 value) {
		throw new UnsupportedOperationException();
	}
}