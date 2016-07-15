
package org.cmg.tapas.xtext.ccsp;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class CCSPSpecificationStandaloneSetup extends CCSPSpecificationStandaloneSetupGenerated{

	public static void doSetup() {
		new CCSPSpecificationStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

