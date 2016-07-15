package org.cmg.tapas.core.graph.visitor;

import java.io.Serializable;

public class HistoryElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6833170031920739098L;
	private String src;
	private String act;
	private String dest;
	
	public HistoryElement(){}
	
	public HistoryElement(String src, String act, String dest){
		this.src = src;
		this.act = act;
		this.dest = dest;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}
	
	public String toString(){
		return src+"--"+act+"->"+dest;
	}		
}
