/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.HashSet;

import org.cmg.tapas.core.graph.filter.Filter;

/**
 * @author loreti
 *
 */
public class CCSPChannelSet implements Filter<CCSPAction> {

	protected HashSet<CCSPChannel> set;
	
	public CCSPChannelSet( CCSPChannel ... channels ) {
		this.set = new HashSet<CCSPChannel>();
		for (int i = 0; i < channels.length; i++) {
			this.set.add(channels[i]);
		}
	}
	
	@Override
	public boolean check(CCSPAction t) {
		CCSPChannel c = t.getChannel();
		return ((c!=null)&&(set.contains(c)));
	}

	
	
}
