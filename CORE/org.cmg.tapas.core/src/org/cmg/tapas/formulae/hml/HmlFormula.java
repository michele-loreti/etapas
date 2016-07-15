/**
 * 
 */
package org.cmg.tapas.formulae.hml;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cmg.tapas.formulae.lmc.ActionLoader;
import org.cmg.tapas.formulae.lmc.LogicalFormula;
import org.cmg.tapas.formulae.lmc.Proof;
import org.cmg.tapas.core.graph.ActionInterface;
import org.cmg.tapas.core.graph.filter.Filter;
import org.cmg.tapas.pa.Process;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @author loreti
 *
 */
public abstract class HmlFormula<X extends Process<X, Y>,Y extends ActionInterface> 
						implements Cloneable,LogicalFormula<X,Y> {

	private String name="";
	private String description="";
	public abstract boolean satisfies( X s ); 
	public abstract Proof<X,Y> getProof( X s );
	private static int fixPointCounter = 0;
	
	public final HmlFormula<X,Y> clone() {
		return doClone();
	}

	protected abstract HmlFormula<X, Y> doClone();
	
	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> existsEventually( Filter<Y> filter ,  HmlFormula<X,Y> formula ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlOrFormula<X, Y>(
				new HmlDiamondFormula<X,Y>( filter , result.getReference() ) ,
				formula
			)
		);
		return result;
	}
	
	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> forAllEventually( Filter<Y> filter ,  HmlFormula<X,Y> formula ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlOrFormula<X, Y>(
				new HmlAndFormula<X, Y>(
						new HmlDiamondFormula<X,Y>( filter , new HmlTrue<X,Y>() ) ,
						new HmlBoxFormula<X,Y>(filter, result.getReference()) 
				) ,
				formula
			)
		);
		return result;
	}
	
	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> existsGlobally( Filter<Y> filter ,  HmlFormula<X,Y> formula ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(true,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlAndFormula<X, Y>(
				new HmlOrFormula<X,Y>(
					new HmlBoxFormula<X,Y>( filter , new HmlFalse<X,Y>() ) ,
					new HmlDiamondFormula<X,Y>( filter , result.getReference() ) 	
				) ,
				formula
			)
		);
		return result;
	}

	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> forAllGlobally( Filter<Y> filter ,  HmlFormula<X,Y> formula ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(true,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlAndFormula<X, Y>(
				new HmlBoxFormula<X,Y>( filter , result.getReference() ) ,
				formula
			)
		);
		return result;
	}

	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> exitstUntil( HmlFormula<X,Y> formula1 , Filter<Y> filter , HmlFormula<X,Y> formula2   ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlOrFormula<X, Y>(
				formula2 , 
				new HmlAndFormula<X, Y>( 
					formula1 ,
					new HmlDiamondFormula<X, Y>(
							filter, 
							result.getReference()
					) 
				)
			)
		);
		return result;
	}

	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> forAllUntil( HmlFormula<X,Y> formula1 , Filter<Y> filter , HmlFormula<X,Y> formula2   ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlOrFormula<X, Y>(
				formula2 , 
				new HmlAndFormula<X, Y>( 
					formula1 ,
					new HmlAndFormula<X , Y> (
						new HmlDiamondFormula<X, Y>(
								filter, 
								new HmlTrue<X, Y>()
						) ,
						new HmlBoxFormula<X, Y>(
								filter, 
								result.getReference()
						) 
					)
				)
			)
		);
		return result;
	}

	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> exitstUntil( HmlFormula<X,Y> formula1 , Filter<Y> filter1 , Filter<Y> filter2 , HmlFormula<X,Y> formula2   ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlOrFormula<X, Y>(
				formula1 , 
				new HmlAndFormula<X, Y>( 
					new HmlDiamondFormula<X,Y>( filter2 , formula2 ) ,
					new HmlDiamondFormula<X, Y>(
							filter1 , 
							result.getReference()
					) 
				)
			)
		);
		return result;
	}

	public static <X extends Process<X, Y>,Y extends ActionInterface> HmlFormula<X,Y> forAllUntil( HmlFormula<X,Y> formula1 , Filter<Y> filter1 , Filter<Y> filter2 , HmlFormula<X,Y> formula2   ) {
		HmlFixPoint<X, Y> result = new HmlFixPoint<X, Y>(false,"X"+fixPointCounter++);
		result.setSubformula( 
			new HmlAndFormula<X, Y>(
				formula1 , 
				new HmlAndFormula<X, Y>( 
					new HmlOrFormula<X,Y>( 
						new HmlDiamondFormula<X,Y>(filter1, new HmlTrue<X,Y>() ) ,	
						new HmlDiamondFormula<X,Y>(filter2, new HmlTrue<X,Y>() )	
					) ,
					new HmlAndFormula<X,Y>(
						new HmlBoxFormula<X,Y>( filter2 , formula2 ) ,
						new HmlBoxFormula<X, Y>(
								filter1 , 
								result.getReference()
						) 
					)
				)
			)
		);
		return result;
	}
	

	public void visit( FormulaVisitor<X,Y> visitor ) {
		doVisit(visitor);
	}

	protected abstract void doVisit(FormulaVisitor<X,Y> visitor);

	@Override
	public String toString() {
		return _toString();
	}

	protected abstract String _toString();

	@Override
	public int hashCode() {
		return (toString()).hashCode();
	}	
	
	public static <S extends Process<S,A>, A extends ActionInterface>  HmlFormula<S,  A> loadFormula( InputSource i , ActionLoader<S,A> loader) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(true);
		SAXParser parser = spf.newSAXParser();
		XMLFormulaHandler<S,A> xfh = new XMLFormulaHandler<S,A>(loader);
		parser.parse(i, xfh);
		return xfh.getFormula(); 
	}

	public abstract String getUnicode();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract String operator();
	
	@Override
	public boolean equals(Object o){
		return _equals(o);
	}
	
	protected abstract boolean _equals(Object o);
	
}
