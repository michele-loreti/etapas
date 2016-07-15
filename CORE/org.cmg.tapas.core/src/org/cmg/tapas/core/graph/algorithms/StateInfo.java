package org.cmg.tapas.core.graph.algorithms;


/**
 * Classe per mantenere le informazioni di uno stato
 * prodotte durante una visita in profondit� del grafo con
 * un certo filtro.
 * 
 * @param <S> tipo degli stati
 * 
 * @author Guzman Tierno
 **/	
public class StateInfo<S> {
	// Stato di cui si tengono le informazioni
	private S state;		
	// indice della componente dello stato
	private int dfs;	
	// flag di convergenza dello stato
	private boolean convergent;
	// indice della componente dello stato
	private int root;	
	// dimensione della componente dello stato oppure 0 se
	// lo stato � solo e senza loop
	private int compSize;
	// flag che indica se lo stato � una foglia
	private boolean leaf;	
	// rank dello stato
	private int rank = UNSET;
	
	public static final int UNSET = -3;

	/**
	 * Constructor StateInfo.
	 * Tutti i metodi di modifica hanno volutamente visibilit�
	 * pacchetto.
	 *
	 * @param state Stato di cui questo oggetto conosce le informazioni.
	 * @param dfs Indice dello stato nella visita in profondit�
	 * @param convergent Flag di convergenza dello stato
	 */
	StateInfo(S state, int dfs, boolean convergent) {
		this.state = state;
		this.dfs = dfs;
		this.convergent = convergent;
	}

	/**
	 * Method getState
	 */
	public S getState() {
		return state; 
	}

	/**
	 * Method setState
	 */
	void setState(S state) {
		this.state = state; 
	}

	/**
	 * Rende l'indice della componente fortemente connessa 
	 * di appartenenza dello stato (rispetto al filtro
	 * usato per generare le informazioni).
	 * Gli indici delle componenti non sono incrementali
	 * e non partono da 0 ma identificano univocamente le componenti.
	 */
	public int getComponent() {
		return root; 
	}

	/**
	 * Setta l'indice della componente fortemente connessa 
	 * di appartenenza dello stato.
	 */
	void setComponent(int root) {
		this.root = root; 
	}

	/**
	 * Rende la dimensione della componente fortemente connessa 
	 * a cui lo stato appartiene (rispetto al filtro
	 * usato per generare le informazioni).
	 * Se rende 0 significa 
	 * che lo stato � l'unico elemento della componente 
	 * e non ha selfloops. Se rende 1 significa 
	 * che lo stato � l'unico elemento della componente 
	 * ed ha selfloops. Se rende <tt>n>1</tt> significa 
	 * che lo stato appartiene a una componente di <tt>n</tt> stati.
	 */
	public int getComponentSize() {
		return compSize; 
	}

	/**
	 * Method setCompSize
	 */
	void setComponentSize(int compSize) {
		this.compSize = compSize; 
	}

	/**
	 * Rende il flag di convergenza dello stato.
	 * In altri termini rende true se dallo stato partono
	 * sequenze infinite di azioni (le azioni considerate
	 * sono quelle del filtro che � stato usato per generare
	 * le informazioni).
	 */
	public boolean isConvergent() {
		return convergent; 
	}

	/**
	 * Method setConvergent
	 */
	void setConvergent(boolean convergent) {
		this.convergent = convergent; 
	}

	/**
	 * Rende true se lo stato � una foglia rispetto
	 * alle azioni accettate dal filtro usato per generare
	 * le informazioni.
	 */
	public boolean isLeaf() {
		return leaf; 
	}

	/**
	 * Method setLeaf
	 */
	void setLeaf(boolean leaf) {
		this.leaf = leaf; 
	}

	/**
	 * Rende il rank dello stato (rispetto al filtro
	 * usato per generare le informazioni).
	 * Se il valore di ritorno � StateInfo.UNSET significa
	 * che il rank non � stato calcolato. Il rank viene
	 * calcolato dal metodo GraphAnalyzer.computeRank().
	 */
	public int getRank() {
		return rank; 
	}

	/**
	 * Method setRank
	 */
	void setRank(int rank) {
		this.rank = rank; 
	}

	/**
	 * Method toString 
	 */
	@Override 
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		
		buffer.append("comp=");
		buffer.append(root);
		buffer.append(", ");
		
		buffer.append("size=");
		buffer.append(compSize);
		buffer.append(", ");
		
		buffer.append("conv=");
		buffer.append(convergent);
		buffer.append(", ");
		
		buffer.append("leaf=");
		buffer.append(leaf);
		buffer.append(", ");
		
		buffer.append("rank=");
		buffer.append(rank);
		
		buffer.append(")\n");		
		return buffer.toString();
	}	
	
	/** 
	 * Dice se questo oggetto � uguale a quello specificato. 
	 **/
	@Override 
	public boolean equals(Object o) {
		if( o==null || !(o instanceof StateInfo) )
			return false;
		
		StateInfo other = (StateInfo) o;
		return
			state == other.state &&
			root == other.root &&
			dfs == other.dfs &&
			compSize == other.compSize &&
			convergent == other.convergent &&
			leaf == other.leaf &&
			rank == other.rank;
	}
	
	
} 
