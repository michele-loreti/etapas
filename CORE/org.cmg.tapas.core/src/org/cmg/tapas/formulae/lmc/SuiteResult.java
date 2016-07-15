package org.cmg.tapas.formulae.lmc;

import java.util.Collection;
import java.util.Vector;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;
//import org.cmg.tapas.util.GarbageCollectorFrameAdapter;

public class SuiteResult<X extends Process<X,Y>,Y extends ActionInterface>
                         extends Suite<X, Y> {

	private static int EXETIMES_COLUMN = 3;
	private static int SELECT_COLUMN = 2;
	private static int COLUMNS = 4;
	
	private X process;
	private Vector<Boolean> results;
	private Vector<Float> exeTimes;
	
	public SuiteResult(X process) {
		this(process, null);
	}

	public SuiteResult() {
		super();
		results = new Vector<Boolean>();
		exeTimes = new Vector<Float>();
	}
	
	public SuiteResult(X process, Collection<LogicalFormula<X,Y>> c) {
		super(c);
		this.process = process;
		results = new Vector<Boolean>(c.size());
		exeTimes = new Vector<Float>(c.size());
		reset();
	}
	
	public void setParameters(X process, Collection<LogicalFormula<X,Y>> c){
		this.process = process;		
		if(c != null)
			formulae = new Vector<LogicalFormula<X,Y>>(c);
		else
			formulae = new Vector<LogicalFormula<X,Y>>();
		enabled = new Vector<Boolean>();
		results = new Vector<Boolean>(c.size());
		exeTimes = new Vector<Float>(c.size());
		reset();
	}
	
	private void reset() {
		for( int i=0 ; i<formulae.size() ; i++ ) {
			results.add(i, null);
			exeTimes.add(i, null);
		}
		fireTableDataChanged();
	}
	
	public X getProcess(){
		return process;
	}

	public int getColumnCount() {
		return COLUMNS;
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
		case 1:
		case 2:
		case 3:
			return String.class;
		default:
			return null;
		}
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == DESCRIPTION_COLUMN-1) {
			return "Property Name";
		}
		if (columnIndex == FORMULA_COLUMN-1) {
			return "Formula";
		}
		if (columnIndex == SELECT_COLUMN) {
			return "Result";
		}
		if (columnIndex == EXETIMES_COLUMN) {
			return "Exe. time";
		}
		return null;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return formulae.get(rowIndex).getName();
		case 1:
			return formulae.get(rowIndex).getUnicode();
		case 2:
			Boolean b = results.get(rowIndex);
			if (b == null) {
				return " ";
			}
			return (b?"Yes":"No");
		case 3:
			Float r = exeTimes.get(rowIndex);
			if(r == null)
				return " ";
			return r.toString()+" s";
		default:
			return "BO";
		}
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == SELECT_COLUMN) {
			results.set(rowIndex, (Boolean) aValue);
			fireTableDataChanged();
		}
	}

	public void addFormula(LogicalFormula<X, Y> f) {
		formulae.add(f);
		results.add(null);
		exeTimes.add(null);
		fireTableRowsInserted(0, formulae.size());
	}

	public void doCheck() {
		for( int i=0 ; i<formulae.size() ; i++ ) {
			long start = System.currentTimeMillis();
			results.set(i, formulae.get(i).satisfies(process));
			float exeTime = (System.currentTimeMillis()-start);
			exeTimes.set(i, exeTime/1000);
			fireTableDataChanged();
			//GarbageCollectorFrameAdapter.execute();
		}
	}
	
	public Proof<X, Y> getProof(int index){
		return formulae.get(index).getProof(process);
	}
}
