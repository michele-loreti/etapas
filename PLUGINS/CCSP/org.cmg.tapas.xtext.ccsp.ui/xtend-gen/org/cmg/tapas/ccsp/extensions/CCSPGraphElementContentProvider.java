package org.cmg.tapas.ccsp.extensions;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.cmg.tapas.views.TAPAsGraphElementContentProvider;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NextProcess;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.NilProcess;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessDeclaration;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.ProcessReference;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateDeclaration;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.StateReference;
import org.cmg.tapas.xtext.ccsp.cCSPSpecification.Transition;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;

@SuppressWarnings("all")
public class CCSPGraphElementContentProvider implements TAPAsGraphElementContentProvider {
  private ProcessDeclaration process;
  
  @Override
  public Object[] getElements(final Object inputElement) {
    Object[] _xblockexpression = null;
    {
      if ((inputElement instanceof ProcessDeclaration)) {
        this.process = ((ProcessDeclaration) inputElement);
      }
      Object[] _elvis = null;
      LinkedList<Object> _collectProcessStates = null;
      if (this.process!=null) {
        _collectProcessStates=this.collectProcessStates(this.process);
      }
      Set<Object> _set = null;
      if (_collectProcessStates!=null) {
        _set=IterableExtensions.<Object>toSet(_collectProcessStates);
      }
      Object[] _array = null;
      if (_set!=null) {
        _array=_set.toArray();
      }
      if (_array != null) {
        _elvis = _array;
      } else {
        Object[] _newArrayOfSize = new Object[0];
        _elvis = _newArrayOfSize;
      }
      _xblockexpression = _elvis;
    }
    return _xblockexpression;
  }
  
  @Override
  public void dispose() {
    this.process = null;
  }
  
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
  }
  
  @Override
  public Object[] getRelationships(final Object source, final Object dest) {
    Object[] _switchResult = null;
    boolean _matched = false;
    if (source instanceof StateDeclaration) {
      _matched=true;
      EList<Transition> _actions = ((StateDeclaration)source).getActions();
      final Function1<Transition, Boolean> _function = (Transition a) -> {
        return Boolean.valueOf(this.leadsTo(a, dest));
      };
      Iterable<Transition> _filter = IterableExtensions.<Transition>filter(_actions, _function);
      Set<Transition> _set = IterableExtensions.<Transition>toSet(_filter);
      _switchResult = _set.toArray();
    }
    return _switchResult;
  }
  
  public LinkedList<Object> collectProcessStates(final ProcessDeclaration process) {
    LinkedList<Object> _xblockexpression = null;
    {
      LinkedList<Object> stateList = new LinkedList<Object>();
      EList<StateDeclaration> _states = process.getStates();
      stateList.addAll(_states);
      EList<StateDeclaration> _states_1 = process.getStates();
      final Function2<LinkedList<Object>, StateDeclaration, LinkedList<Object>> _function = (LinkedList<Object> l, StateDeclaration s) -> {
        LinkedList<Object> _xblockexpression_1 = null;
        {
          EList<Transition> _actions = s.getActions();
          final Function1<Transition, NextProcess> _function_1 = (Transition a) -> {
            return a.getNext();
          };
          List<NextProcess> _map = ListExtensions.<Transition, NextProcess>map(_actions, _function_1);
          final Function1<NextProcess, Boolean> _function_2 = (NextProcess x) -> {
            boolean _isLocal = this.isLocal(x);
            return Boolean.valueOf((!_isLocal));
          };
          Iterable<NextProcess> _filter = IterableExtensions.<NextProcess>filter(_map, _function_2);
          Iterables.<Object>addAll(l, _filter);
          _xblockexpression_1 = l;
        }
        return _xblockexpression_1;
      };
      _xblockexpression = IterableExtensions.<StateDeclaration, LinkedList<Object>>fold(_states_1, stateList, _function);
    }
    return _xblockexpression;
  }
  
  public boolean isLocal(final NextProcess p) {
    boolean _switchResult = false;
    boolean _matched = false;
    if (p instanceof StateReference) {
      _matched=true;
      _switchResult = true;
    }
    if (!_matched) {
      if (p instanceof ProcessReference) {
        _matched=true;
        ProcessDeclaration _process = ((ProcessReference)p).getProcess();
        _switchResult = Objects.equal(_process, this.process);
      }
    }
    if (!_matched) {
      if (p instanceof NilProcess) {
        _matched=true;
        _switchResult = false;
      }
    }
    if (!_matched) {
      _switchResult = false;
    }
    return _switchResult;
  }
  
  public boolean leadsTo(final Transition t, final Object o) {
    boolean _xblockexpression = false;
    {
      NextProcess target = t.getNext();
      boolean _switchResult = false;
      boolean _matched = false;
      if (target instanceof StateReference) {
        _matched=true;
        StateDeclaration _state = ((StateReference)target).getState();
        _switchResult = Objects.equal(_state, o);
      }
      if (!_matched) {
        if (target instanceof ProcessReference) {
          _matched=true;
          _switchResult = (Objects.equal(target, o) || (this.isLocal(target) && Objects.equal(((ProcessReference)target).getState(), o)));
        }
      }
      if (!_matched) {
        if (target instanceof NilProcess) {
          _matched=true;
          _switchResult = Objects.equal(target, o);
        }
      }
      _xblockexpression = _switchResult;
    }
    return _xblockexpression;
  }
}
