package org.cmg.tapas.xtext.slts.tests

import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.InjectWith
import org.cmg.tapas.xtext.slts.SimpleLtsInjectorProvider
import org.eclipse.xtext.junit4.util.ParseHelper
import org.cmg.tapas.xtext.slts.simpleLts.Model
import com.google.inject.Inject
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper
import org.junit.Test
import static org.junit.Assert.*


@InjectWith(SimpleLtsInjectorProvider)
@RunWith(XtextRunner)
class TestParser {
	
	@Inject
	ParseHelper<Model> parser

	@Inject extension CompilationTestHelper
	
	
	@Test
	def testSimpleLts() {
		'''
			model SimpleLTS;
			
			action insertCoin;
			action insertCoin;
			action selectTea;
			action selectCoffee;
			
			noCoin {
				insertCoin -> oneCoin;
			}
			
			state oneCoin {
				insertCoin -> twoCoin;
				selectCoffee -> noCoin;
			}
			
			state twoCoin {
				selectCoffee -> oneCoin;
				selectTea -> noCoin;
			}
			
			
			label ready: oneCoin, twoCoin;
			
			hml formula eventuallyReady = min X . #[ready] | ( < * > true & [ * ] X ) ;			
		'''.compile[
			compiledClass.newInstance => [
				assertNotNull( it )
			]	
		]
	}
	
	
}