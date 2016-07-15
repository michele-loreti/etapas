package org.cmg.tapas.core.graph;


/**
 * Implementazione base dell'interfaccia LabelledStateI
 * per gli stati etichettati, ossia gli stati con informazione aggiunta. 
 * 
 * @param <L> tipo per l'etichetta (informazione aggiuntiva) dello stato.
 *
 * @author Guzman Tierno
 */
public class LabelledState<L>  
implements LabelledStateI<L> {
	private String name;
	private L label;
	
	public LabelledState(String name, L label) {
		this.name = name;
		this.label = label;
	}
	
	public L getLabel() {
		return label;
	}
	
	public String getId() {
		return name;
	}

	public void setId(String name) {
		this.name = name;
	}		
	
	@Override
	public String toString() {
		return name + ":" + label.toString(); 
	}
}


