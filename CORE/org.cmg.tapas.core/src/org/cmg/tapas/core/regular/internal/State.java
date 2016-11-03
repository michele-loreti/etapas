package org.cmg.tapas.core.regular.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class State<A> implements RegularStateI<A>{
	
	private boolean accepting;
	
	private Map<A,Set<RegularStateI<A>>> stepFunction;
	
	public State() {
		this(false);
	}

	public State(boolean accepting) {
		this.accepting = accepting;
		this.stepFunction = new HashMap<>();
	}
	
	@Override
	public Set<RegularStateI<A>> apply(A c) {
		return stepFunction.getOrDefault(c, new HashSet<>());
	}
	
	@Override
	public boolean isAccepting() {
		return accepting;
	}
	
	@Override
	public Set<A> symbols() {
		return stepFunction.keySet();
	}

	public void addTransition(A c, State<A> state) {
		Set<RegularStateI<A>> next = stepFunction.get(c);
		if (next == null) {
			next = new HashSet<>();
			stepFunction.put(c, next);
		}
		next.add(state);
	}
	
	public void addTransition(A c, Set<RegularStateI<A>> next) {
		stepFunction.put(c,next);
	}
}