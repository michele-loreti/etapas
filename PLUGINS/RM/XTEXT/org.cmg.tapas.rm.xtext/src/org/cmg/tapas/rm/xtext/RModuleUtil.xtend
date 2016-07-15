package org.cmg.tapas.rm.xtext

import static extension org.eclipse.xtext.EcoreUtil2.*
import org.cmg.tapas.rm.xtext.module.Module
import org.cmg.tapas.rm.xtext.module.DeclarationRenaming
import java.util.Set
import org.cmg.tapas.rm.xtext.module.Function
import java.util.HashSet
import java.beans.Expression
import org.cmg.tapas.rm.xtext.module.Or
import com.google.common.collect.Sets
import org.cmg.tapas.rm.xtext.module.And
import org.cmg.tapas.rm.xtext.module.Equal
import org.cmg.tapas.rm.xtext.module.Less
import org.cmg.tapas.rm.xtext.module.LessEq
import org.cmg.tapas.rm.xtext.module.GreaterEq
import org.cmg.tapas.rm.xtext.module.Greater
import org.cmg.tapas.rm.xtext.module.NotEqual
import org.cmg.tapas.rm.xtext.module.Plus
import org.cmg.tapas.rm.xtext.module.Minus
import org.cmg.tapas.rm.xtext.module.Division
import org.cmg.tapas.rm.xtext.module.Multiplication
import org.cmg.tapas.rm.xtext.module.NotExpression
import org.cmg.tapas.rm.xtext.module.LiteralExpression
import org.cmg.tapas.rm.xtext.module.Constant
import org.cmg.tapas.rm.xtext.module.NumberExpression
import org.cmg.tapas.rm.xtext.module.TrueExpression
import org.cmg.tapas.rm.xtext.module.FalseExpression

class RModuleUtil {


	def static moduleDependencies( Module m ) {
		var modules = <Module>newArrayList()
		var current = getRenamedBody(m)
		while (current != null && !modules.contains(current)) {
			modules.add(current)
			current = getRenamedBody(current)
		}
		modules
	}
	
	def static getRenamedBody( Module m ) {
		var body = m.body
		switch body {
			DeclarationRenaming: body.module
			default: null
		}		
	}

	def static dispatch Set<Function> collectFunctions( Expression e , HashSet<Function> visited ) {
		newHashSet()
	}

	def static dispatch Set<Function> collectFunctions( NumberExpression e , HashSet<Function> visited ) {
		newHashSet()
	}

	def static dispatch Set<Function> collectFunctions( Or e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}

	def static dispatch Set<Function> collectFunctions( And e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
	
	def static dispatch Set<Function> collectFunctions( Equal e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
	
	def static dispatch Set<Function> collectFunctions( Less e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}

	def static dispatch Set<Function> collectFunctions( LessEq e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}

	def static dispatch Set<Function> collectFunctions( GreaterEq e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}

	def static dispatch Set<Function> collectFunctions( Greater e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}

	def static dispatch Set<Function> collectFunctions( NotEqual e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
	
	def static dispatch Set<Function> collectFunctions( Plus e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
 
	def static dispatch Set<Function> collectFunctions( Minus e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
 
	def static dispatch Set<Function> collectFunctions( Division e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
 
	def static dispatch Set<Function> collectFunctions( Multiplication e , HashSet<Function> visited ) {
		Sets::union(e.left.collectFunctions(visited),e.right.collectFunctions(visited))
	}
 
	def static dispatch Set<Function> collectFunctions( NotExpression e , HashSet<Function> visited ) {
		e.arg.collectFunctions(visited)
	}

	def static dispatch Set<Function> collectFunctions( LiteralExpression e , HashSet<Function> visited ) {
		var r = e.reference
		switch (r) {
			Function:
				if (visited.contains(r)) {
					newHashSet(r)
				} else {
					visited.add( r )
					Sets::union(newHashSet(r),r.body.collectFunctions(visited))
				}
			default: newHashSet()				
		}
	}	
	
	def static dispatch Set<Constant> collectConstants( Expression e , HashSet<Constant> visited ) {
		newHashSet()
	}

	def static dispatch Set<Constant> collectConstants( TrueExpression e , HashSet<Constant> visited ) {
		newHashSet()
	}

	def static dispatch Set<Constant> collectConstants( FalseExpression e , HashSet<Constant> visited ) {
		newHashSet()
	}

	def static dispatch Set<Constant> collectConstants( Or e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}

	def static dispatch Set<Constant> collectConstants( And e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
	
	def static dispatch Set<Constant> collectConstants( Equal e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
	
	def static dispatch Set<Constant> collectConstants( Less e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}

	def static dispatch Set<Constant> collectConstants( LessEq e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}

	def static dispatch Set<Constant> collectConstants( GreaterEq e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}

	def static dispatch Set<Constant> collectConstants( Greater e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}

	def static dispatch Set<Constant> collectConstants( NotEqual e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
	
	def static dispatch Set<Constant> collectConstants( Plus e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
 
	def static dispatch Set<Constant> collectConstants( Minus e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
 
	def static dispatch Set<Constant> collectConstants( Division e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
 
	def static dispatch Set<Constant> collectConstants( Multiplication e , HashSet<Constant> visited ) {
		Sets::union(e.left.collectConstants(visited),e.right.collectConstants(visited))
	}
 
	def static dispatch Set<Constant> collectConstants( NotExpression e , HashSet<Constant> visited ) {
		e.arg.collectConstants(visited)
	}
	
	def static dispatch Set<Constant> collectConstants( NumberExpression e , HashSet<Constant> visited ) {
		newHashSet()
	}
	

	def static dispatch Set<Constant> collectConstants( LiteralExpression e , HashSet<Constant> visited ) {
		var r = e.reference
		switch (r) {
			Constant:
				if (visited.contains(r)) {
					newHashSet(r)
				} else {
					visited.add( r )
					Sets::union(newHashSet(r),r.expression.collectConstants(visited))
				}
			default: newHashSet()				
		}
	}
	
	
	
	
	
}