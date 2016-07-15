/**
 * 
 */
package org.lic.validation;

import org.lic.secsi.TypeDef;

/**
 * @author Michael
 *
 */
public abstract class Type {
	
	
	private static SecsiBoolean BOOLEAN = new SecsiBoolean();
	private static SecsiInteger INTEGER = new SecsiInteger();
	
	public static SecsiBoolean getBoolean() {
		return BOOLEAN;
	}
	
	public static SecsiInteger getInteger() {
		return INTEGER;
	}
	
	public static SecsiCustomType getCustomType( TypeDef def ) {
		return new SecsiCustomType(def);
	}
	
	public static class SecsiBoolean extends Type {

		@Override
		public String toString() {
			return "boolean";
		}
		
	}
	
	public static class SecsiInteger extends Type {
				
		@Override
		public String toString() {
			return "integer";
		}

	}
	
	public static class SecsiCustomType extends Type {
		
		private TypeDef def;
		
		private SecsiCustomType(TypeDef def) {
			this.def = def;
		}

		@Override
		public boolean equals(Object arg0) {
			if (arg0 instanceof SecsiCustomType) {
				return def.getName().equals(((SecsiCustomType) arg0).def.getName());
			}
			return false;
		}

		@Override
		public String toString() {
			return def.getName();
		}
		
		
				
	}
	
	

}
