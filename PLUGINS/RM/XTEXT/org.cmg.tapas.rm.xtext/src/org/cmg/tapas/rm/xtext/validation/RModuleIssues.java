/**
 * 
 */
package org.cmg.tapas.rm.xtext.validation;

/**
 * @author loreti
 *
 */
public enum RModuleIssues {
	
	TYPE_ERROR("Type_error"),
	ILLEGAL_ACCESS("Variable_Illegal_access"),
	ILLEGAL_USE("Variable_Illegal_use");
	
	
	@Override
	public String toString() {
		return this.code;
	}

	private String code;
	
	
	private RModuleIssues( String code ) {
		this.code = code;
	}
	

}
