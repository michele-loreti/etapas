/**
 * 
 */
package org.cmg.tapas.core.mc;

/**
 * @author loreti
 *
 */
public class ModelCheckingResult<S> {
	
	private boolean result;
	
	private S counterExample;
	
	public ModelCheckingResult( boolean result ) {
		this(result,null);
	}

	public ModelCheckingResult(boolean result, S counterExample) {
		this.result = result;
		this.counterExample = counterExample;
	}

	public boolean isResult() {
		return result;
	}

	public S getCounterExample() {
		return counterExample;
	}

}
