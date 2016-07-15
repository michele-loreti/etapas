
package org.lic;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class SecsiStandaloneSetup extends SecsiStandaloneSetupGenerated{

	public static void doSetup() {
		new SecsiStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

