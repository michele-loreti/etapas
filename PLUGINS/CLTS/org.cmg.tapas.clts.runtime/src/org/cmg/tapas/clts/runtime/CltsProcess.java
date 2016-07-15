package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.pa.Process;

public abstract class CltsProcess implements Process<CltsProcess, CltsAction>{
	
	public enum ProcessType {
		STATE,
		INTERLEAVING,
		COMPOSITION,
		RENAMING
	}
	
	private HashMap<CltsAction, Set<CltsProcess>> next;
	
	private String str;
	private Integer hashcode;
	
	@Override
	public HashMap<CltsAction, Set<CltsProcess>> getNext( ) {
		if( next == null ){
			next = _getNext( );
		}
		return next;
	}

	
	protected abstract HashMap<CltsAction, Set<CltsProcess>> _getNext( );
	
	protected void updateNext(HashMap<CltsAction, Set<CltsProcess>> next, CltsAction a, Set<CltsProcess> set){
		Set<CltsProcess> current = next.get(a);
		if( current == null ){
//			current = new HashSet<CltsProcess>();
			next.put(a, set);
		} else {
			current.addAll(set);			
		}
	}
	
	protected Set<CltsProcess> doParallel(Set<CltsProcess> next, CltsProcess q){
		HashSet<CltsProcess> set = new HashSet<CltsProcess>();
		for(CltsProcess p : next ){
			set.add(new CltsInterleaving(p,q));
		}
		return set;
	}
	
	protected Set<CltsProcess> doParallel(CltsProcess p, Set<CltsProcess> next){
		HashSet<CltsProcess> set = new HashSet<CltsProcess>();
		for(CltsProcess q : next ){
			set.add(new CltsInterleaving(p,q));
		}
		return set;
	}
	
	protected Set<CltsProcess> doCooperate(Set<CltsProcess> set, Filter<CltsAction> actionSet,CltsProcess right){
		HashSet<CltsProcess> result = new HashSet<CltsProcess>();
		for( CltsProcess p : set ){
			result.add(new CltsCooperation(p, actionSet,right));
		}
		return result;
	}

	protected Set<CltsProcess> doCooperate(CltsProcess left, Filter<CltsAction> actionSet,Set<CltsProcess> set){
		HashSet<CltsProcess> result = new HashSet<CltsProcess>();
		for( CltsProcess p : set ){
			result.add(new CltsCooperation(left, actionSet,p));
		}
		return result;
	}

	protected Set<CltsProcess> doCooperate(Set<CltsProcess> leftSet, Filter<CltsAction> actionSet,Set<CltsProcess> rightSet){
		HashSet<CltsProcess> result = new HashSet<CltsProcess>();
		if (( leftSet == null )||(rightSet == null)) {
			return result;
		}
		for( CltsProcess p : leftSet ){
			for( CltsProcess q : rightSet ) {
				result.add(new CltsCooperation(p, actionSet,q));
			}
		}
		return result;
	}

	protected void doSync(HashMap<CltsAction, Set<CltsProcess>> next,
			Set<CltsProcess> leftNext, Set<CltsProcess> rightNext, CltsAction act) {
		if ((leftNext==null)||(rightNext == null)) {
			return ;
		}
		HashSet<CltsProcess> set = new HashSet<CltsProcess>();
		for (CltsProcess p: leftNext) {
			for(CltsProcess q: rightNext) {
				set.add(new CltsInterleaving(p,q));
			}
		}
		updateNext(next, act, set);
		
	}
	
	protected Set<CltsProcess> doRenaming(Set<CltsProcess> set, Map<CltsAction, CltsAction> renaming){
		HashSet<CltsProcess> result = new HashSet<CltsProcess>();
		for(CltsProcess p : set){
			result.add(new CltsRenaming(p, renaming));
		}
		return result;
	}
	
	/**
	 * Metodo che controlla se almeno uno stato di cui si compone il CltsProcess
	 * è contenuto nell'insieme set. Questo metodo viene utilizzato nella creazione
	 * di una nuova istanza della classe Filter<S> per controllare se un CltsProcess 
	 * soddisfa una data proposizione atomica.
	 * 
	 * @param 	set: insieme degli stati che soddisfano la proposizione
	 * @return	true: se il CltsProcess soddisfa la proposizione
	 * 			false: altrimenti 
	 */
	public abstract boolean sat( Set<CltsState> set);
	
	public abstract ProcessType getProcessType();
	
	public abstract Set<String> getLabels();
	
	@Override
	public boolean isDeadlock() {
		HashMap<CltsAction, Set<CltsProcess>> next = this.getNext();
		return (next == null)||(next.size()==0);
	}

	@Override
	public String toString() {
		if (str == null) {
			str = _toString();
		}
		return str;
	}
	
	protected abstract String _toString();

	@Override
	public int hashCode() {
		if (hashcode == null) {
			hashcode = _hashcode();
		}
		return hashcode;
	}
	
	protected abstract Integer _hashcode();
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CltsProcess)) {
			return false;
		}
		CltsProcess q = (CltsProcess) obj;
		if (q.getProcessType()!=getProcessType()) {
			return false;
		}
		return _equals(q);
	}
	
	protected abstract boolean _equals(CltsProcess q);
	
	

}
