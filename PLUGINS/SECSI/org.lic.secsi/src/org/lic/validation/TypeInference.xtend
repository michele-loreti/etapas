package org.lic.validation

import org.lic.secsi.AndExpression
import org.lic.secsi.BoolConstant
import org.lic.secsi.BooleanType
import org.lic.secsi.IntConstant
import org.lic.secsi.IntegerType
import org.lic.secsi.MulExpression
import org.lic.secsi.Negation
import org.lic.secsi.OrExpression
import org.lic.secsi.ReferenceInExpression
import org.lic.secsi.Relation
import org.lic.secsi.SumExpression
import org.lic.secsi.TypeElement
import org.lic.secsi.TypeReference
import org.lic.secsi.Variable
import org.lic.secsi.Channel
import org.lic.secsi.ChannelsDeclaration
import org.lic.secsi.TypeExpression
import org.lic.secsi.Expression
import org.lic.secsi.ReferenceableElement
import org.lic.secsi.TypeDef
import org.lic.secsi.Constant

class TypeInference {
	
	def dispatch Type getType(Object o) {
		return null
	}
	
	def dispatch Type  getType( OrExpression e ) {
		Type::boolean
	}

	def dispatch Type getType( AndExpression e ) {
		Type::boolean
	}

	def dispatch Type getType( Relation r ) {
		Type::boolean
	}	
	
	def dispatch Type getType( BoolConstant c ) {
		Type::boolean
	}
	
	def dispatch Type getType( IntConstant i ) {
		Type::integer
	}
	
	def dispatch Type getType( Negation n ) {
		Type::boolean
	}
	
	
	def dispatch Type getType( TypeElement e ) {
		e.eContainer.type
	}
	
	def dispatch Type getType( Variable tv) {
		tv.type.type
	}
	def dispatch Type getType( Constant c) {
		c.type.type
	}
	
//	def dispatch Type getReferenceType( ReferenceableElement e ) {
//		return null;
//	}
	
	def dispatch Type getType( TypeReference r ) {
		Type::getCustomType(r.reference)
	}
	
	def dispatch Type getType( IntegerType t ) {
		Type::integer
	}	
	
	def dispatch Type getType( BooleanType b ) {
		Type::boolean
	}
	
	def dispatch Type getType( SumExpression e ) {
		Type::integer
	}

	def dispatch Type getType( MulExpression e ) {
		Type::integer
	}

	def dispatch Type getType( ReferenceInExpression r ) {
		r.reference.type
	}
	
	def dispatch Type getType(Channel c){
		c.type.type
	}	
	
	def dispatch Type getType( TypeDef td ) {
		Type::getCustomType(td)
	}
	
	def dispatch isAnInput(Expression e) {
		return false;
	}
	
	
	def dispatch isAnInput(ReferenceInExpression ref)  {
		switch ref.reference {
			Variable: true
			default: false
		}
	}
}