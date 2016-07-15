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
class TestTypeChecker {
	
	@Inject extension ParseHelper<Model>
	
	@Inject extension ValidationTestHelper
	
	@Test
	def void testLeftPlusTypeError() {
		'''
		specification Test:
		
		const test = (true + 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.plus , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightPlusTypeError() {
		'''
		specification Test:
		
		const test = (4 + true)
		'''.parse.assertError( ModulePackage::eINSTANCE.plus , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLeftMinusTypeError() {
		'''
		specification Test:
		
		const test = (true - 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.minus , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightMinusTypeError() {
		'''
		specification Test:
		
		const test = (4 - true)
		'''.parse.assertError( ModulePackage::eINSTANCE.minus , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLeftMulTypeError() {
		'''
		specification Test:
		
		const test = (true * 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.multiplication , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightMulTypeError() {
		'''
		specification Test:
		
		const test = (4 * true)
		'''.parse.assertError( ModulePackage::eINSTANCE.multiplication , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLeftDivTypeError() {
		'''
		specification Test:
		
		const test = (true / 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.division , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightDivTypeError() {
		'''
		specification Test:
		
		const test = (4 / true)
		'''.parse.assertError( ModulePackage::eINSTANCE.division , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLeftOrTypeError() {
		'''
		specification Test:
		
		const test = (true or 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.or , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightOrTypeError() {
		'''
		specification Test:
		
		const test = (4 or true)
		'''.parse.assertError( ModulePackage::eINSTANCE.or , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLeftAndTypeError() {
		'''
		specification Test:
		
		const test = (true and 4)
		'''.parse.assertError( ModulePackage::eINSTANCE.and , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightAndTypeError() {
		'''
		specification Test:
		
		const test = (4 and true)
		'''.parse.assertError( ModulePackage::eINSTANCE.and , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testRightNotTypeError() {
		'''
		specification Test:
		
		const test = !4
		'''.parse.assertError( ModulePackage::eINSTANCE.notExpression , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testEqualLeftTypeError() {
		'''
		specification Test:
		
		const test = (true == 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.equal , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testEqualRightTypeError() {
		'''
		specification Test:
		
		const test = (true == 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.equal , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testNotEqualLeftTypeError() {
		'''
		specification Test:
		
		const test = (true != 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.notEqual , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testNotEqualRightTypeError() {
		'''
		specification Test:
		
		const test = (true != 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.notEqual , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testLessLeftTypeError() {
		'''
		specification Test:
		
		const test = (true < 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.less , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testLessRightTypeError() {
		'''
		specification Test:
		
		const test = (true < 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.less , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testLessEqualLeftTypeError() {
		'''
		specification Test:
		
		const test = (true <= 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.lessEq , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testLessEqualRightTypeError() {
		'''
		specification Test:
		
		const test = (4 <= true);
		'''.parse.assertError( ModulePackage::eINSTANCE.lessEq , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testGreaterLeftTypeError() {
		'''
		specification Test:
		
		const test = (true > 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.greater , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testGreaterRightTypeError() {
		'''
		specification Test:
		
		const test = (4 > true);
		'''.parse.assertError( ModulePackage::eINSTANCE.greater , ModuleValidator::TYPE_ERROR )
	}
	
	@Test
	def void testGreaterEqualLeftTypeError() {
		'''
		specification Test:
		
		const test = (true >= 4);
		'''.parse.assertError( ModulePackage::eINSTANCE.greaterEq , ModuleValidator::TYPE_ERROR )
	}

	@Test
	def void testGreaterEqualRightTypeError() {
		'''
		specification Test:
		
		const test = (4 >= true);
		'''.parse.assertError( ModulePackage::eINSTANCE.greaterEq , ModuleValidator::TYPE_ERROR )
	}
		
	def checkModel(CharSequence prog) {
        val model = prog.parse
        Assert::assertNotNull(model)
        model.assertNoErrors
        model
    }
	
}