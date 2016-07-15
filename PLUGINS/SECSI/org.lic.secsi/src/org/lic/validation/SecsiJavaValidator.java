package org.lic.validation;

import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.validation.Check;
import org.lic.secsi.ActionModality;
import org.lic.secsi.Agent;
import org.lic.secsi.AndExpression;
import org.lic.secsi.Assignment;
import org.lic.secsi.Channel;
import org.lic.secsi.ChannelsDeclaration;
import org.lic.secsi.Expression;
import org.lic.secsi.IfThenElse;
import org.lic.secsi.MulExpression;
import org.lic.secsi.Negation;
import org.lic.secsi.OrExpression;
import org.lic.secsi.Relation;
import org.lic.secsi.SecsiPackage;
import org.lic.secsi.Signal;
import org.lic.secsi.SumExpression;
import org.lic.secsi.Switch_Case;
import org.lic.secsi.VarDeclaration;
import org.lic.secsi.Variable;
import org.lic.secsi.While;

public class SecsiJavaValidator extends AbstractSecsiJavaValidator {
	
	TypeInference ti = new TypeInference();

//	@Check
//	public void checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.getName().charAt(0))) {
//			warning("Name should start with a capital", MyDslPackage.Literals.GREETING__NAME);
//		}
//	}
	
//	@Check
//	public void checkParamsProcess (Agent process) {
//		if(process.getParams().size() == 0)
//			warning("Hai creato un processo senza parametri",null);
//	}
	
	@Check
	public void OrCheck( OrExpression e ) {
		Expression left = e.getLeft();
		if (left != null) {
			Type t = ti.getType(left);
			if (t == null) {
				return ;
			}
			if (t != Type.getBoolean()) {
				error("Type error: expected "+Type.getBoolean()+" is "+t,SecsiPackage.Literals.OR_EXPRESSION__LEFT);
			}
		}
		Expression right = e.getRight();
		if (right != null) {
			Type t = ti.getType(right);
			if (t==null) {
				return ;
			}
			if (t != Type.getBoolean()) {
				error("Type error: expected "+Type.getBoolean()+" is "+t,SecsiPackage.Literals.OR_EXPRESSION__RIGHT);
			}
		}
	}
	
	@Check
	public void AndCheck(AndExpression e){
		Expression left = e.getLeft();
		if (left != null) {
			Type t = ti.getType(left);
			if (t == null) {
				return ;
			}
			if (t != Type.getBoolean()) {
				error("Type error: expected "+Type.getBoolean()+" is "+t,SecsiPackage.Literals.AND_EXPRESSION__LEFT);
			}
		}
		Expression right = e.getRight();
		if (right != null) {
			Type t = ti.getType(right);
			if (t == null) {
				return ;
			}
			if (t != Type.getBoolean()) {
					error("Type error: expected "+Type.getBoolean()+" is "+t,SecsiPackage.Literals.AND_EXPRESSION__RIGHT);
			}
		}
	}
	@Check
	public void RelationCheck(Relation r){
		Expression left = r.getLeft();
		if (left != null) {
			Type t = ti.getType(left);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
				error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.RELATION__LEFT);
			}
		}
		Expression right = r.getRight();
		if (right != null) {
			Type t = ti.getType(right);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
					error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.RELATION__RIGHT);
			}
		}
	}
	
	@Check 
	public void CheckSum (SumExpression se){
		Expression left = se.getLeft();
		if (left != null) {
			Type t = ti.getType(left);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
				error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.SUM_EXPRESSION__RIGHT);
			}
		}
		Expression right = se.getRight();
		if (right != null) {
			Type t = ti.getType(right);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
					error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.SUM_EXPRESSION__RIGHT);
			}
		}
	}

	// AC
	
	@Check 
	public void MulSum (MulExpression me){
		Expression left = me.getLeft();
		if (left != null) {
			Type t = ti.getType(left);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
				error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.MUL_EXPRESSION__LEFT);
			}
		}
		Expression right = me.getRight();
		if (right != null) {
			Type t = ti.getType(right);
			if (t == null) {
				return ;
			}
			if (t != Type.getInteger()) {
					error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.MUL_EXPRESSION__RIGHT);
			}
		}
	}

	@Check
	public void NegationCheck(Negation n){
		Type type=ti.getType(n.getArg());
		if (type == null) {
			return ;
		}
		if(type!=Type.getBoolean()){
			error("Type error: expected "+Type.getBoolean()+" is "+type,SecsiPackage.Literals.NEGATION__ARG);
		}
	}
	
	
	@Check
	public void VarDecCheck(VarDeclaration v){		
		Type left=ti.getType(v.getVariable());
		Expression exp=v.getInit();
		if(exp != null){
			Type right = ti.getType(exp);
			if (right == null) {
				return ;
			}
			if(!left.equals(right)){
			error("Type error: expected "+left+" is "+right,SecsiPackage.Literals.VAR_DECLARATION__INIT);
			}
		} 		//range
	}
	
	
	
	@Check
	public void IfThenElseCheck (IfThenElse ite){
		if(ite.getExpression()==null){
			return ;
		}
		else{
		Type guard=ti.getType(ite.getExpression());
		if(guard!=Type.getBoolean()){
			error("Type error: expected "+Type.getBoolean()+" is "+guard,SecsiPackage.Literals.IF_THEN_ELSE__EXPRESSION);
			}
		}
		
}
	
	@Check
	public void WhileCheck (While w){
		if(w.getExpresion()==null){
			return ;
		}
		else{
		Type guard=ti.getType(w.getExpresion());
		if(guard!=Type.getBoolean()){
			error("Type error: expected "+Type.getBoolean()+" is "+guard,SecsiPackage.Literals.WHILE__EXPRESION);
			}
		
		}
	}

	@Check
	public void SwitchCheck(Switch_Case sc){
		if(sc.getValue()==null){
			return ;
		}
		else{
				EList<Switch_Case> iterator = sc.getCases();
				int i=iterator.size()-1;
				while(i>=0){
					Switch_Case c=iterator.get(i);
					if(c.getValue()==null){
						//|| !ti.isAnInput(c.getValue())){
						error("Expected a pattern for this case",SecsiPackage.Literals.SWITCH_CASE__CASES);
						}
					i=i-1;
					
				}
		}
	}
	
	
	
	
	
	@Check
	public void Action(org.lic.secsi.Action a){
		Channel c = a.getChannel();
		if (!(c.getType() instanceof Signal)) {
			Type first=ti.getType(a.getChannel());
				if(a.getE()==null){
					error("Insert a value into the channel for value passing ",SecsiPackage.Literals.ACTION__E);
				}
				else{
						Type second=ti.getType(a.getE());
						if (second == null) {
							return ;
						}
						if(!first.equals(second)){
							error("Type error: expected "+first+" is "+second,SecsiPackage.Literals.ACTION__E);
						}
				}
			}
	}
	

	@Check
	public void InputAction(org.lic.secsi.Action a){
		Channel c = a.getChannel();
		if (!(c.getType() instanceof Signal)&&(a.getModality()==ActionModality.INPUT ) ) {
			if(a.getE()==null){
				error("Insert a value into the channel for value passing ",SecsiPackage.Literals.ACTION__E);
			}
			else{
				if (!ti.isAnInput(a.getE())) {
					error("A variable is expected!",SecsiPackage.Literals.ACTION__E);
				}
			}
		}
	}


	@Check
	public void SignalAction(org.lic.secsi.Action a){
		Channel c = a.getChannel();
		if ((c.getType() instanceof Signal)&&(a.isNotSignal())) {
			error("No value can be sent over a signal channel!",SecsiPackage.Literals.ACTION__E);
		} 
		//	channel=[Channel] modality=ActionModality ('('e=Expression')')? ';'
	}

	/*@Check
	public void Assignment(Assignment as, org.lic.secsi.Action a){
		if(a.getChannel().getName().equals(as.getId().getName())){
			error("Channel can't be initialized with =",SecsiPackage.Literals.ASSIGNMENT__ID);
		}
	}*/
	
	
	@Check
	public void AssignmentCheck(Assignment a){
		//if(!(a.getId().getClass().getName().equals("Channel"))){
			Variable v=a.getId();
			
			if((v == null)||(a.getIn()==null)){
				return ;
			}
			else{  //controlla che non sia un canale
				Type left=ti.getType(v);
				Type right=ti.getType(a.getIn());
				if ((left == null)||(right == null)) {
					return ;
				}
				if(left!=right){
					error("Type error: expected "+left+" is "+right,SecsiPackage.Literals.ASSIGNMENT__ID);
				}
			}
	}

}
	//	else{
	//		error("Specify modality action ! or ?, not equality ",SecsiPackage.Literals.ASSIGNMENT__ID);
	//	}
	
	
	/*@Check
	public void IntCheck (IntConstant i){
		
		Type t = ti._getType(i);
		if(t != Type.getInteger()){
			error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.INT_CONSTANT__VALUE);
		}
		
	}
	
	@Check
	public void BooleanCheck (BoolConstant b){
		
		Type t = ti._getType(b);
		if(t != Type.getBoolean()){
			error("Type error: expected "+Type.getInteger()+" is "+t,SecsiPackage.Literals.INT_CONSTANT__VALUE);
		}
		
	}*/
	

		
	
/*
BaseExpression returns Expression:
  IntConstant 
 | BoolConstant 
 | ReferenceInExpression
;


ReferenceInExpression:
	reference=[ReferenceableElement]
;
 */
	
	

