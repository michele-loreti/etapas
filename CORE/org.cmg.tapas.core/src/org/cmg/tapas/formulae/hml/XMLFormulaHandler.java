/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import org.cmg.tapas.formulae.lmc.ActionLoader;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.pa.Process;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;


/**
 * @author loreti
 *
 */
public class XMLFormulaHandler<S extends Process<S, A>, A extends ActionInterface> 
					 extends DefaultHandler2 {

	String description = "";
	HashSet<A> actions;
	Stack<HmlFormula<S, A>> stack;
	Stack<HashSet<A>> action_stack;
	Stack<String> fix_point;
	Hashtable<String, HmlFixPoint<S, A>> fix_points;
	private ActionLoader<S, A> loader;
	
	public XMLFormulaHandler(ActionLoader<S, A> loader) {
		this.stack = new Stack<HmlFormula<S,A>>();
		this.action_stack = new Stack<HashSet<A>>();
		this.fix_point = new Stack<String>();
		this.fix_points = new Hashtable<String, HmlFixPoint<S,A>>();
		this.loader = loader;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("and")) {
			HmlFormula<S, A> f1 = stack.pop();
			HmlFormula<S, A> f2 = stack.pop();
			stack.push(new HmlAndFormula<S, A>(f1,f2));
			return ;
		}
		if (qName.equals("or")) {
			HmlFormula<S, A> f1 = stack.pop();
			HmlFormula<S, A> f2 = stack.pop();
			stack.push(new HmlOrFormula<S, A>(f1,f2));
			return ;
		}
		if (qName.equals("not")) {
			HmlFormula<S, A> f = stack.pop();
			stack.push(new HmlNotFormula<S, A>(f));
			return ;
		}
		if (qName.equals("diamond")) {
			HashSet<A> a = action_stack.pop();
			HmlFormula<S, A> f = stack.pop();
			stack.push(new HmlDiamondFormula<S, A>(a,f));
			return ;
		}
		if (qName.equals("box")) {
			HashSet<A> a = action_stack.pop();
			HmlFormula<S, A> f = stack.pop();
			stack.push(new HmlBoxFormula<S, A>(a,f));
			return ;
		}
		if (qName.equals("min")) {
			String x = fix_point.pop();
			HmlFixPoint<S, A> f = fix_points.get(x);
			f.setSubformula(stack.pop());
			stack.push(f);
			fix_points.remove(x);
		}
		if (qName.equals("max")) {
			String x = fix_point.pop();
			HmlFixPoint<S, A> f = fix_points.get(x);
			f.setSubformula(stack.pop());
			stack.push(f);
			fix_points.remove(x);
		}
		if (qName.equals("actionlist")) {
			action_stack.push(actions);
			actions = new HashSet<A>();
		}
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("formula")) {
			description = attributes.getValue("description");
		}
		if (qName.equals("min")) {
			String x = attributes.getValue("var");
			fix_point.push(x);
			HmlFixPoint<S, A> f = new HmlFixPoint<S, A>(false,x);				
			if (fix_points.put(x, f) != null) {
				throw new SAXException(); 
			}
			stack.push(f);
		}
		if (qName.equals("max")) {
			String x = attributes.getValue("var");
			fix_point.push(x);
			HmlFixPoint<S, A> f = new HmlFixPoint<S, A>(true,x);				
			if (fix_points.put(x, f) != null) {
				throw new SAXException(); 
			}
			stack.push(f);
		}
		if (qName.equals("true")) {
			stack.push(new HmlTrue<S, A>());
			return ;
		}
		if (qName.equals("false")) {
			stack.push(new HmlFalse<S, A>());
			return ;
		}
		if (qName.equals("ref")) {
			String v = attributes.getValue("var");
			HmlFixPoint<S, A> f = fix_points.get(v);
			if (v == null) {
				stack.push(new HmlFalse<S, A>());
			} else {
				stack.push(f.getReference());
			}
			return ;
		}
		if (qName.equals("all")) {
			action_stack.push(new HashSet<A>());
			return ;
		}
		if (qName.equals("actionlist")) {
			actions = new HashSet<A>();
		}
		//TODO: Sistemare i casi seguenti
		if (qName.equals("in")) {
			actions.add(loader.getAction( "in" , attributes.getValue("channel") ));
			return ;
		}
		if (qName.equals("out")) {
			actions.add(loader.getAction( "out" , attributes.getValue("channel") ));
			return ;
		}
		if (qName.equals("tau")) {
			actions.add(loader.getAction( "tau" , null ));
			return ;
		}
	}
	
	public HmlFormula<S, A> getFormula() {
		try {
			HmlFormula<S, A> f = stack.peek();
			if (description != null) {
				f.setDescription(description);
			}
			return f;
		} catch (RuntimeException e) {
			return null;
		}
	}	

}
