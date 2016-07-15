/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author loreti
 *
 */
public class CCSPRenaming extends CCSPProcess {

	protected CCSPProcess process;
	protected Map<CCSPChannel,CCSPChannel> renaming;
	
	/**
	 * @param process
	 * @param renaming
	 */
	public CCSPRenaming(CCSPProcess process,
			Map<CCSPChannel, CCSPChannel> renaming) {
		super();
		this.process = process;
		this.renaming = renaming;
	}

	/**
	 * @param process
	 * @param renaming
	 */
	public CCSPRenaming(CCSPProcess process,
			CCSPChannel[] src ,
			CCSPChannel[] trg ) {
		super();
		this.process = process;		
		this.renaming = new HashMap<CCSPChannel, CCSPChannel>();
		for( int i=0 ; i<src.length ; i++) {
			this.renaming.put(src[i], trg[i]);
		}
	}

	@Override
	protected HashMap<CCSPAction, Set<CCSPProcess>> _getNext() {
		HashMap<CCSPAction, Set<CCSPProcess>> next = new HashMap<CCSPAction, Set<CCSPProcess>>();
		HashMap<CCSPAction, Set<CCSPProcess>> internalNext = process.getNext();
		for (CCSPAction a : internalNext.keySet()) {
			CCSPAction b = a.rename(renaming);
			updateNext(next, b , doRenaming( internalNext.get(a) , renaming ));
		}
		return next;
	}

	@Override
	public ProcessType getProcessType() {
		return ProcessType.RENAMING;
	}

	@Override
	protected boolean _equals(CCSPProcess q) {
		try {
			CCSPRenaming rq = (CCSPRenaming) q;
			return process.equals(rq.process)&&renaming.equals(rq.renaming);
		} catch (ClassCastException e) {
			return false;
		}
	}

	@Override
	protected Integer _hashcode() {
		return process.hashCode()^renaming.hashCode();
	}

	@Override
	protected String _toString() {
		return process.toString()+renaming.toString();
	}

}
