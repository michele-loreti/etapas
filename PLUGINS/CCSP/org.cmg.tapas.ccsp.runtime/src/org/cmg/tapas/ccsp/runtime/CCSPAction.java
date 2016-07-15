/**
 * 
 */
package org.cmg.tapas.ccsp.runtime;

import java.util.Map;

import org.cmg.tapas.core.graph.ActionInterface;

/**
 * @author loreti
 *
 */
public abstract class CCSPAction implements ActionInterface {

	@Override
	public final int compareTo(ActionInterface o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTau() {
		// TODO Auto-generated method stub
		return false;
	}

	public static final CCSPAction TAU = new TauAction();

	public abstract CCSPAction getComplementaryAction();
	
	private static class TauAction extends CCSPAction {

		@Override
		public CCSPAction getComplementaryAction() {
			return null;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return obj==CCSPAction.TAU;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "tau";
		}

		@Override
		public CCSPAction rename(Map<CCSPChannel, CCSPChannel> map) {
			return this;
		}

		@Override
		public boolean isTau() {
			return true;
		}
		
	}
	
	public static class InputAction extends CCSPAction {
		
		private CCSPChannel channel;
		
		public InputAction( CCSPChannel channel ) {
			this.channel = channel;
		}

		@Override
		public CCSPAction getComplementaryAction() {
			return new OutputAction(channel);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof InputAction)) {
				return false;
			}
			return (channel.equals(((InputAction) arg0).channel));
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return ~channel.hashCode();
		}
		
		public CCSPChannel getChannel() {
			return channel;
		}

		@Override
		public CCSPAction rename(Map<CCSPChannel, CCSPChannel> map) {
			CCSPChannel c = map.get(channel);
			if (c == null) {
				return this;
			}
			return new InputAction(c);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "?"+channel.toString();
		}
		
	}
	
	public static class OutputAction extends CCSPAction {

		private CCSPChannel channel;
		
		public OutputAction( CCSPChannel channel ) {
			this.channel = channel;
		}
		
		@Override
		public CCSPAction getComplementaryAction() {
			return new InputAction(channel);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object arg0) {
			if (!(arg0 instanceof OutputAction)) {
				return false;
			}
			return (channel.equals( ((OutputAction) arg0).channel) );
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return channel.hashCode();
		}

		public CCSPChannel getChannel() {
			return channel;
		}

		@Override
		public CCSPAction rename(Map<CCSPChannel, CCSPChannel> map) {
			CCSPChannel c = map.get(channel);
			if (c == null) {
				return this;
			}
			return new OutputAction(c);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "!"+channel.toString();
		}

	}

	public CCSPChannel getChannel() {
		return null;
	}

	public abstract CCSPAction rename(Map<CCSPChannel,CCSPChannel> map);

}
