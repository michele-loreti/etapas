/**
 * 
 */
package org.cmg.tapas.formulae.lmc;


import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;


/**
 * @author Michele Loreti & Francesco Calzolai
 *
 */
public class Suite<X extends Process<X,Y>,Y extends ActionInterface> 
					extends AbstractTableModel 
					implements Iterable<LogicalFormula<X, Y>> {

	private static final long serialVersionUID = 1L;
	

	private static int ENABLED_COLUMN = 0;
	protected static int DESCRIPTION_COLUMN = 1;
	protected static int FORMULA_COLUMN = 2;
	private static int COLUMNS = 3;

	protected Vector<Boolean> enabled;
	protected Vector<LogicalFormula<X, Y>> formulae; 
	
	public Suite() {
		this(null);
	}
	
	public Suite(Collection<LogicalFormula<X,Y>> c) {
		if(c != null)
			formulae = new Vector<LogicalFormula<X,Y>>(c);
		else
			formulae = new Vector<LogicalFormula<X,Y>>();
		enabled = new Vector<Boolean>();
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Boolean.class;
		case 1:
		case 2:
			return String.class;
		default:
			return null;
		}
	}

	public int getColumnCount() {
		return COLUMNS;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex == ENABLED_COLUMN) {
			return "Enable";
		}
		if (columnIndex == DESCRIPTION_COLUMN) {
			return "Property Name";
		}
		if (columnIndex == FORMULA_COLUMN) {
			return "Formula";
		}
		return null;
	}

	public int getRowCount() {
		return formulae.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return enabled.get(rowIndex);
		case 1:
			return formulae.get(rowIndex).getName();
		case 2:
			return formulae.get(rowIndex).getUnicode();
		default:
			return "";
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == ENABLED_COLUMN;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == ENABLED_COLUMN) {
			enabled.set(rowIndex, (Boolean) aValue);
			fireTableDataChanged();
		}
	}

	public void addFormula(LogicalFormula<X, Y> f) {
		enabled.add(true);
		formulae.add(f);
		fireTableRowsInserted(0, formulae.size());
	}
		
	private Vector<LogicalFormula<X, Y>> getEnabled(){
		Vector<LogicalFormula<X, Y>> res = new Vector<LogicalFormula<X, Y>>();
		for( int i=0 ; i<formulae.size() ; i++ ) { 
			if(enabled.get(i)){
				res.add(formulae.get(i));
			}
		}		
		return res;
	}
	
	public void doCheck(X s, SuiteResult<X, Y> sr) {
		Vector<LogicalFormula<X, Y>> tmp = getEnabled();
		if(tmp.size()>0){
			JProgressBar jpb = new JProgressBar(JProgressBar.HORIZONTAL,0,formulae.size());
			jpb.setVisible(true);
			sr.setParameters(s, tmp);			
			sr.doCheck();
			jpb.setVisible(false);
		}
	}

	public Iterator<LogicalFormula<X, Y>> iterator() {
		return formulae.iterator();
	}

	@Override
	public String toString() {
		String toReturn = "";
		for (LogicalFormula<X, Y> f : formulae) {
			toReturn += f+"\n";
		}
		return toReturn;
	}

	public String toUnicode() {
		String toReturn = "";
		for (LogicalFormula<X, Y> f : formulae) {
			toReturn += f.getUnicode()+"\n";
		}
		return toReturn;
	}

	public void addAll(Suite<X, Y> suite) {
		for (LogicalFormula<X, Y> formula : suite) {
			addFormula(formula);
		}
	}

	public void clear() {
		int size = formulae.size();
		formulae.removeAllElements();
		fireTableRowsDeleted(0, size);
	}
}
