package org.lic.secsi.tests

import com.google.inject.Inject
import junit.framework.Assert
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.InjectWith
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import java.io.FileInputStream
import java.io.File
import java.io.IOException
import java.nio.channels.FileChannel
import org.eclipse.emf.common.util.URI
import org.eclipse.xtext.resource.XtextResourceSet
import com.google.inject.Provider;
import org.lic.SecsiInjectorProvider
import org.lic.secsi.Program

@InjectWith(typeof(SecsiInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestParser {
	
	@Inject
	ParseHelper<Program> parser
	
	@Inject extension ValidationTestHelper
	
	@Inject
	Provider<XtextResourceSet> resourceSetProvider;
	
	
	@Test
	def void testPaerserEmpty() {
		parser.parse(
		'''
		process test() {
			boolean flag;
			flag = true||flag;
		}
		'''						
		).assertNoErrors
	}
	
	@Test
	def void testParseBrp() {
		parser.parse( new FileInputStream("Examples/leaderelection.secsi") , URI::createFileURI("Examples/leaderelection.secsi") , null , resourceSetProvider.get()  ).assertNoErrors
	}
}