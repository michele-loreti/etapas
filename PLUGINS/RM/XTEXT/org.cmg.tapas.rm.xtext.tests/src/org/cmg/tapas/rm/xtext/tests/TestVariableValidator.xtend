package org.cmg.tapas.rm.xtext.tests

import org.eclipse.xtext.junit4.XtextRunner
import org.junit.runner.RunWith
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.util.ParseHelper
import org.cmg.tapas.rm.xtext.ModuleInjectorProvider
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import com.google.inject.Inject
import org.junit.Test
import org.cmg.tapas.rm.xtext.module.Model
import org.junit.Assert
import org.cmg.tapas.rm.xtext.module.ModulePackage
import org.cmg.tapas.rm.xtext.validation.ModuleValidator

@InjectWith(typeof(ModuleInjectorProvider))
@RunWith(typeof(XtextRunner))
class TestVariableValidator {
	
	@Inject extension ParseHelper<Model>
	
	@Inject extension ValidationTestHelper

		
	@Test
	def void testWrongAccessToLocalVariable() {
		'''
			specification Test:
			
			module A:
			variables:
				x: int(0..1) init 0;
			rules:
			[] true -> { y <- 1 };
			endmodule
			
			module B:
			variables:
				y: int(0..1) init 0;
			rules:
			[] true -> { y <- 1 };
			endmodule
		'''.parse.assertError(ModulePackage::eINSTANCE.command,ModuleValidator::ILLEGAL_ACCESS_TO_LV,'Module A cannot update variable y defined in module B!')
	}
	
	@Test
	def void testAccessToLocalVariable() {
		'''
			specification Test:
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { x <- 1 };
			endmodule
			
			module B:
			variables:
				y: int(0..1);
			rules:
			[] true -> { y <- 1 };
			endmodule
		'''.checkModel
	}
	
	@Test
	def void testWrongAccessToGlobalVariable() {
			'''
			specification Test:
			
			variable y: int(0..1);
			
			action a;
			
			module A:
			variables:
				x: int(0..1) init 0;
			rules:
			[a] true -> { y <- 1 };
			endmodule
			
		'''.parse.assertError(ModulePackage::eINSTANCE.command,ModuleValidator::ILLEGAL_ACCESS_TO_GV,'Global variable y cannot be updated in a named transition!')
	}

	@Test
	def void testAccessToGlobalVariable() {
			'''
			specification Test:
			
			variable y: int(0..1);
			
			action a;
			
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { y <- 1 };
			endmodule
			
		'''.checkModel
	}

	@Test
	def void testWrongUseOfFunction() {
			'''
			specification Test:
			
			function f =  2*x;
			
			const c = f;
			
			action a;
			
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { x <- 1 };
			endmodule
			
		'''.parse.assertError(ModulePackage::eINSTANCE.literalExpression,ModuleValidator::ILLEGAL_USE_OF_FUNCTION,'Functions cannot occur in constants declarations!')
	}

	@Test
	def void testWrongUseOfVariablesAndFunctionsInType() {
			'''
			specification Test:
			
			function f =  2*x;
			
			variable y: int( 0 .. 32);
			
			variable x: int( f .. y );
			
			action a;
			
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { x <- 1 };
			endmodule
			
		'''.parse => [
				assertError(ModulePackage::eINSTANCE.literalExpression,ModuleValidator::ILLEGAL_USE_OF_FUNCTION,'Functions cannot occur in type declarations!')
				assertError(ModulePackage::eINSTANCE.literalExpression,ModuleValidator::ILLEGAL_USE_OF_VARIABLE,'Variables cannot occur in type declarations!')
			]
			
	}
	
	@Test
	def void testWrongUseOfVariable() {
			'''
			specification Test:
			
			const c = 2*x;
			
			action a;
			
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { x <- 1 };
			endmodule
			
			'''.parse.assertError(ModulePackage::eINSTANCE.literalExpression,ModuleValidator::ILLEGAL_USE_OF_VARIABLE,'Variables cannot occur in constants declarations!')
	}
	
	@Test
	def void testRecursiveConstantsDeclaration() {
			'''
			specification Test:
			
			const a = 2*b;
			const b = 2*a;

			'''.parse => [ 
					assertError(ModulePackage::eINSTANCE.constant,ModuleValidator::CIRCULAR_CONSTANT_DECLARATION,'Circular definition of constant a!')
					assertError(ModulePackage::eINSTANCE.constant,ModuleValidator::CIRCULAR_CONSTANT_DECLARATION,'Circular definition of constant b!')
				]
	}
	
	@Test
	def void testRecursiveFunctionDeclaration() {
			'''
			specification Test:
			
			function f = x*g;
			function g = x*f;
			
			action a;
			
			module A:
			variables:
				x: int(0..1);
			rules:
			[] true -> { x <- 1 };
			endmodule
			
			'''.parse => [
				assertError(ModulePackage::eINSTANCE.function,ModuleValidator::CIRCULAR_FUNCTION_DECLARATION,'Circular definition of function f!')
				assertError(ModulePackage::eINSTANCE.function,ModuleValidator::CIRCULAR_FUNCTION_DECLARATION,'Circular definition of function f!')
			]
	}
	
	@Test
	def void testCircularModuleDefinition() {
			'''
			specification Test:
						
			module A:
			variables:
				x: int(0..1);
			rules:
			B[ y -> x ]
			endmodule
			
			module B:
			variables:
				y: int(0..1);
			rules:
			C[ z -> y ]
			endmodule
			
			module C:
			variables:
				z: int(0..1);
			rules:
			A[ x -> z ]
			endmodule
						
			'''.parse => [
				assertError(ModulePackage::eINSTANCE.module,ModuleValidator::CIRCULAR_MODULE_RENAMING,'Circular renaming of module A!')
				assertError(ModulePackage::eINSTANCE.module,ModuleValidator::CIRCULAR_MODULE_RENAMING,'Circular renaming of module B!')
				assertError(ModulePackage::eINSTANCE.module,ModuleValidator::CIRCULAR_MODULE_RENAMING,'Circular renaming of module C!')
			]
		
	}
	
	@Test
	def void testWrongRenamingLocalVariable() {
		'''
		specification Test:

		variable g: int(0..10);
		
		module A:
		variables:
			x: int(0..10);
		rules:
		[] x<10 -> { x <- x+1 };		
		[] x==10 -> { x <- 0 };
		endmodule
		
		
		module B:
		rules:
			A[ x -> g ]		
		endmodule
		'''.parse => [
				assertError(ModulePackage::eINSTANCE.elementRenaming,ModuleValidator::UNCOMPATIBLE_VARIABLE_RENAMING,'Local variable x cannot be renamed in the global variable g!')
		]
	}

	@Test
	def void testRenamingAllLocalVariables() {
		'''
		specification Test:
		module A:
		variables:
			x1: int(0..10);
			x2: int(0..10);
		rules:
		[] x1<10 -> { x1 <- x1+1 };		
		[] x1==10 -> { x1 <- 0 };
		[] x2<10 -> { x2 <- x2+1 };		
		[] x2==10 -> { x2 <- 0 };
		endmodule
		
		
		module B:
		variables:
			y1: int(0..10) init 0;
		rules:
			A[ x1 -> y1 ]		
		endmodule
		'''.parse => [
				assertError(ModulePackage::eINSTANCE.module,ModuleValidator::MISSING_RENAMING_OF_LOCAL_VARIABLE,'Local variable x2 of module A is not renamed!')
		]
	}
			
	@Test
	def void testUnIniettiveRenaming() {
		'''
		specification Test:
		module A:
		variables:
			x1: int(0..10) init 0;
			x2: int(0..10) init 0;
		rules:
		[] x1<10 -> { x1 <- x1+1 };		
		[] x1==10 -> { x1 <- 0 };
		[] x2<10 -> { x2 <- x2+1 };		
		[] x2==10 -> { x2 <- 0 };
		endmodule
		
		
		module B:
		variables:
			y1: int(0..10) init 0;
		rules:
			A[ x1 -> y1 , x2 -> y1 ]		
		endmodule
		'''.parse => [
				assertError(ModulePackage::eINSTANCE.elementRenaming,ModuleValidator::DUPLICATE_VARIABLE_RENAMING,'Renaming function must to be injective!')
		]
	}

	def checkModel(CharSequence prog) {
        val model = prog.parse
        Assert::assertNotNull(model)
        model.assertNoErrors
        model
    }
	
	
	
	
}