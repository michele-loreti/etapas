package org.cmg.tapas.ccsp.extensions;

import org.cmg.tapas.views.TAPAsGraphElementLabelProvider;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Action;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Channel;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.InputAction;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NilProcess;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.OutputAction;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessReference;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateReference;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.TauAction;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Transition;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.widgets.ZestStyles;

@SuppressWarnings("all")
public class CCSPGraphElementLabelProvider extends TAPAsGraphElementLabelProvider {
  @Override
  public Color getColor(final Object rel) {
    return ColorConstants.black;
  }
  
  @Override
  public int getConnectionStyle(final Object rel) {
    return ZestStyles.CONNECTIONS_DIRECTED;
  }
  
  @Override
  public Color getHighlightColor(final Object rel) {
    return ColorConstants.yellow;
  }
  
  @Override
  public int getLineWidth(final Object rel) {
    return 1;
  }
  
  @Override
  public IFigure getTooltip(final Object rel) {
    return null;
  }
  
  @Override
  public boolean fisheyeNode(final Object entity) {
    return false;
  }
  
  @Override
  public Color getBackgroundColour(final Object entity) {
    return null;
  }
  
  @Override
  public Color getBorderColor(final Object entity) {
    return null;
  }
  
  @Override
  public Color getBorderHighlightColor(final Object entity) {
    return null;
  }
  
  @Override
  public int getBorderWidth(final Object entity) {
    return 1;
  }
  
  @Override
  public Color getForegroundColour(final Object entity) {
    return null;
  }
  
  @Override
  public Color getNodeHighlightColor(final Object entity) {
    return null;
  }
  
  @Override
  public String getText(final Object element) {
    String _switchResult = null;
    boolean _matched = false;
    if (element instanceof ProcessReference) {
      _matched=true;
      ProcessDeclaration _process = ((ProcessReference)element).getProcess();
      String _name = _process.getName();
      String _plus = (_name + "[");
      StateDeclaration _state = ((ProcessReference)element).getState();
      String _name_1 = _state.getName();
      String _plus_1 = (_plus + _name_1);
      _switchResult = (_plus_1 + "]");
    }
    if (!_matched) {
      if (element instanceof StateReference) {
        _matched=true;
        StateDeclaration _state = ((StateReference)element).getState();
        _switchResult = _state.getName();
      }
    }
    if (!_matched) {
      if (element instanceof StateDeclaration) {
        _matched=true;
        _switchResult = ((StateDeclaration)element).getName();
      }
    }
    if (!_matched) {
      if (element instanceof NilProcess) {
        _matched=true;
        _switchResult = "nil";
      }
    }
    if (!_matched) {
      if (element instanceof Transition) {
        _matched=true;
        Action _act = ((Transition)element).getAct();
        _switchResult = this.stringOfAct(_act);
      }
    }
    return _switchResult;
  }
  
  public String stringOfAct(final Action a) {
    String _switchResult = null;
    boolean _matched = false;
    if (a instanceof InputAction) {
      _matched=true;
      Channel _channel = ((InputAction)a).getChannel();
      String _name = _channel.getName();
      _switchResult = ("?" + _name);
    }
    if (!_matched) {
      if (a instanceof OutputAction) {
        _matched=true;
        Channel _channel = ((OutputAction)a).getChannel();
        String _name = _channel.getName();
        _switchResult = ("!" + _name);
      }
    }
    if (!_matched) {
      if (a instanceof TauAction) {
        _matched=true;
        _switchResult = "tau";
      }
    }
    return _switchResult;
  }
}
