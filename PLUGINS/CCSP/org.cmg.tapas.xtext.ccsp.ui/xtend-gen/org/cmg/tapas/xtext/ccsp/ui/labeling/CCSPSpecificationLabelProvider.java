/**
 * generated by Xtext
 */
package org.cmg.tapas.xtext.ccsp.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * Provides labels for a EObjects.
 * 
 * see http://www.eclipse.org/Xtext/documentation.html#labelProvider
 */
@SuppressWarnings("all")
public class CCSPSpecificationLabelProvider extends DefaultEObjectLabelProvider {
  @Inject
  public CCSPSpecificationLabelProvider(final AdapterFactoryLabelProvider delegate) {
    super(delegate);
  }
}