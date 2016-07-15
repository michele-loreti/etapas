package org.cmg.tapas.clts.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CltsRenaming extends CltsProcess{
	
	protected CltsProcess process;
	protected Map<CltsAction, CltsAction> renaming;
	
	public CltsRenaming(CltsProcess process, Map<CltsAction, CltsAction> renaming) {
		super();
		this.process = process;
		this.renaming = renaming;
	}
	
	public CltsRenaming(CltsProcess process, CltsAction[] src, CltsAction[] trg){
		super();
		this.process = process;
		this.renaming = new HashMap<CltsAction, CltsAction>();
		for( int i=0 ; i<src.length ; i++){
			this.renaming.put(src[i], trg[i]);
		}
	}
	
	@Override
	protected HashMap<CltsAction, Set<CltsProcess>> _getNext() {
		HashMap<CltsAction, Set<CltsProcess>> next = new HashMap<CltsAction, Set<CltsProcess>>();
		HashMap<CltsAction, Set<CltsProcess>> internalNext = process.getNext(  );
		for(CltsAction a : internalNext.keySet()) {
			CltsAction b = a.rename(renaming);
			updateNext(next, b , doRenaming( internalNext.get(a) , renaming));
		}
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.RENAMING;
	}

	@Override
	protected String _toString() {
		return process.toString()+renaming.toString();
	}

	@Override
	protected Integer _hashcode() {
		return process.hashCode()^renaming.hashCode();
	}

	@Override
	protected boolean _equals(CltsProcess q) {
		try {
			CltsRenaming rq = (CltsRenaming) q;
			return process.equals(rq.process)&&renaming.equals(rq.renaming);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean sat(Set<CltsState> set) {
		return process.sat(set);
	}

	@Override
	public Set<String> getLabels() {
		return process.getLabels();
	}

}
