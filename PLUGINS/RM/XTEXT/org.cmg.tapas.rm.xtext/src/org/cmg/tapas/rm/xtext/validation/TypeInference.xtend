package org.cmg.tapas.rm.xtext.validation

import org.cmg.tapas.rm.xtext.module.And
import org.cmg.tapas.rm.xtext.module.BooleanType
import org.cmg.tapas.rm.xtext.module.Constant
import org.cmg.tapas.rm.xtext.module.Division
import org.cmg.tapas.rm.xtext.module.Equal
import org.cmg.tapas.rm.xtext.module.Expression
import org.cmg.tapas.rm.xtext.module.FalseExpression
import org.cmg.tapas.rm.xtext.module.Function
import org.cmg.tapas.rm.xtext.module.Greater
import org.cmg.tapas.rm.xtext.module.GreaterEq
import org.cmg.tapas.rm.xtext.module.IntegerType
import org.cmg.tapas.rm.xtext.module.Less
import org.cmg.tapas.rm.xtext.module.LessEq
import org.cmg.tapas.rm.xtext.module.LiteralExpression
import org.cmg.tapas.rm.xtext.module.Minus
import org.cmg.tapas.rm.xtext.module.Multiplication
import org.cmg.tapas.rm.xtext.module.NotEqual
import org.cmg.tapas.rm.xtext.module.NotExpression
import org.cmg.tapas.rm.xtext.module.NumberExpression
import org.cmg.tapas.rm.xtext.module.Or
import org.cmg.tapas.rm.xtext.module.Plus
import org.cmg.tapas.rm.xtext.module.TrueExpression
import org.cmg.tapas.rm.xtext.module.Variable
import org.cmg.tapas.rm.xtext.module.VariableType

class TypeInference {
	
	
	def dispatch RMDataType inferType( Expression e ) {
		RMDataType::ERROR
	}

	def dispatch RMDataType inferType( Or e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( And e ) {
		RMDataType::BOOL
	}
	
	def dispatch RMDataType inferType( Equal e ) {
		RMDataType::BOOL
	}
	
	def dispatch RMDataType inferType( Less e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( LessEq e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( GreaterEq e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( Greater e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( NotEqual e ) {
		RMDataType::BOOL
	}
	
	def dispatch RMDataType inferType( Plus e ) {
		RMDataType::INT
	}
 
	def dispatch RMDataType inferType( Minus e ) {
		RMDataType::INT
	}
 
	def dispatch RMDataType inferType( Division e ) {
		RMDataType::INT
	}
 
	def dispatch RMDataType inferType( Multiplication e ) {
		RMDataType::INT
	}
 
	def dispatch RMDataType inferType( TrueExpression e ) {
		RMDataType::BOOL
	}
	
	def dispatch RMDataType inferType( FalseExpression e ) {
		RMDataType::BOOL
	}
	
	def dispatch RMDataType inferType( NotExpression e ) {
		RMDataType::BOOL
	}

	def dispatch RMDataType inferType( NumberExpression e ) {
		RMDataType::INT
	}
	
	def dispatch RMDataType inferType( LiteralExpression e ) {
		var r = e.reference
		switch (r) {
			Constant: r.expression.inferType
			Function: r.body.inferType
			Variable: r.type.getType
		}
	}
	
	def getType( VariableType type ) {
		switch (type) {
			BooleanType: RMDataType::BOOL
			IntegerType: RMDataType::INT
		}
	}
	
}