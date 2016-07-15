/**
 * 
 */
package org.cmg.tapas.extensions;

import org.cmg.tapas.views.LabeledElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * @author loreti
 *
 */
public interface TAPAsModelCheckerProvider {
	
	public LabeledElement[] getProcesses();
	
	public boolean isCatecorized();
	
	public String[] getCategories();
	
	public LabeledElement[] getProcesses( String category );
	
	public String[] getFormulae();
	
	public String[] getFormulaeCategories();
	
	public String[] getFormulae( String category );
	
	public boolean check( Object p , String formula );
	
	public void setModel(EObject model, IFile file);

}
