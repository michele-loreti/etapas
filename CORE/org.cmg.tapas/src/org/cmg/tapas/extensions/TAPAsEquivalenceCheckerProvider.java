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
public interface TAPAsEquivalenceCheckerProvider {
	
	public LabeledElement[] getProcesses();
	
	public boolean isCatecorized();
	
	public String[] getCategories();
	
	public LabeledElement[] getProcesses( String category );
	
	public String[] getEquivalences();
	
	public String[] getAlgorithms( int equivalence );
	
	public boolean check( int equivalence , Object p , Object q );
	
	public boolean check( int equivalence , int algorithm , Object p , Object q );

	public void setModel(EObject model, IFile file);

}
