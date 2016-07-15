package org.cmg.tapas.ccsp.extensions;

import java.util.List;
import org.cmg.tapas.ccsp.extensions.CCSPGraphElementContentProvider;
import org.cmg.tapas.ccsp.extensions.CCSPGraphElementLabelProvider;
import org.cmg.tapas.extensions.TAPAsElementViewProvider;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Model;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class CCSPElementViewProvider implements TAPAsElementViewProvider {
  private Model currentModel;
  
  @Override
  public TAPAsGraphElementContentProvider getContentProvider() {
    return new CCSPGraphElementContentProvider();
  }
  
  @Override
  public String[] getElements() {
    EList<ProcessDeclaration> _processes = this.currentModel.getProcesses();
    final Function1<ProcessDeclaration, String> _function = (ProcessDeclaration p) -> {
      return p.getName();
    };
    List<String> _map = ListExtensions.<ProcessDeclaration, String>map(_processes, _function);
    return ((String[])Conversions.unwrapArray(IterableExtensions.<String>sort(_map), String.class));
  }
  
  @Override
  public Object getInput(final String elementName) {
    EList<ProcessDeclaration> _processes = this.currentModel.getProcesses();
    final Function1<ProcessDeclaration, Boolean> _function = (ProcessDeclaration p) -> {
      String _name = p.getName();
      return Boolean.valueOf(_name.equals(elementName));
    };
    return IterableExtensions.<ProcessDeclaration>findFirst(_processes, _function);
  }
  
  @Override
  public TAPAsGraphElementLabelProvider getLabelProvier() {
    return new CCSPGraphElementLabelProvider();
  }
  
  @Override
  public void setModel(final EObject model) {
    if ((model instanceof Model)) {
      this.currentModel = ((Model)model);
    } else {
      this.currentModel = null;
    }
  }
}
