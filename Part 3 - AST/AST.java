/*Data structures for nodes of Abstract Syntax Tree*/

import java.lang.*;
import java.io.*;
import java.util.*;
import java.lang.Math;

interface ASTNode {
	
}

/*Node for program, points to ArrayList of data (declared variables) and an ArrayList of functions*/
class ProgramNode implements ASTNode {
	ArrayList<IDListNode> data;
	ArrayList<FuncNode> functions;

	ProgramNode(ArrayList<IDListNode> d, ArrayList<FuncNode> f){
		this.data = d;
		this.functions = f;
	}
}

class ParamListNode implements ASTNode {
	boolean isVoid;
	ArrayList<ParamNode> params = new ArrayList<ParamNode>();
	
	ParamListNode(){
		this.isVoid = true;
	}
	
	ParamListNode(ArrayList<ParamNode> p){
		this.params = p;
		this.isVoid = false;
	}
}

class FuncNode implements ASTNode {
	TokenNames typeval;
	String name;
	ParamListNode params;
	ArrayList<IDListNode> data;
	ArrayList<StatementNode> statements;
	
	FuncNode(TokenNames t, String s, ParamListNode p, ArrayList<IDListNode> id, ArrayList<StatementNode> st){
		this.typeval = t;
		this.name = s;
		this.params = p;
		this.data = id;
		this.statements = st;
	}
	
	String getTypeval(){
		if (typeval == TokenNames.Void){
			return "void ";
		}
		else{
			return "int ";
		}
	}
	
	int numOps(){
		int ops = 0;
		Iterator<StatementNode> it = statements.iterator();
		while (it.hasNext()){
			ops = ops + it.next().numOps();
		}
		return ops;
	}
	
}

class TempFuncNode implements ASTNode {
	ArrayList<IDListNode> data = new ArrayList<IDListNode>();
	ArrayList<StatementNode> statements = new ArrayList<StatementNode>();
	
	TempFuncNode(ArrayList<IDListNode> d, ArrayList<StatementNode> s){
		this.data = d;
		this.statements = s;
	}
}

class ParamNode implements ASTNode {
	TokenNames typeval;
	String value;
	
	ParamNode(TokenNames t, String s){
		this.typeval = t;
		this.value = s;
	}
}

class IDNode implements ASTNode {
	Integer number = null;
	String name;
	
	IDNode(String s, int n){
		this.name = s;
		this.number = n;
	}
	
	IDNode(String s){
		this.name = s;
	}
	
	IDNode(int n){
		this.number = n;
	}
}

class IDListNode implements ASTNode {
	TokenNames typeval;
	ArrayList<IDNode> list  = new ArrayList<IDNode>();
	
	IDListNode(){
		
	}
	
}

class StatementNode implements ASTNode {
	int type = 0;
	String id;
	
	IfNode ifnode;
	WhileNode whilenode;
	ReturnNode returnnode;
	ContinueNode continuenode;
	BreakNode breaknode;
	ReadNode readnode;
	WriteNode writenode;
	PrintNode printnode;
	AssignmentNode assignmentnode;
	FuncCallNode funccallnode;
	
	int numOps(){
		if(this.type == 0){
		return 0;
		}
		else if(this.type == 1){
			return ifnode.numOps();
		}
		else if(this.type == 2){
			return whilenode.numOps();
		}
		else if(this.type == 3){
			return returnnode.numOps();
		}
		else if(this.type == 4){
			return 0;
		}
		else if(this.type == 5){
			return 0;
		}
		else if(this.type == 6){
			return 0;
		}
		else if(this.type == 7){
			return writenode.numOps();
		}
		else if(this.type == 8){
			return 0;
		}
		else if(this.type == 9){
			return assignmentnode.numOps();
		}
		else {
			return funccallnode.numOps();
		}
	}
	
	StatementNode(int i){
		type = i;
	}
}

class BlockNode implements ASTNode {
	ArrayList<StatementNode> statements;

	BlockNode(ArrayList<StatementNode> s){
		this.statements = s;
	}
	
	int numOps(){
		int ops = 0;
		Iterator<StatementNode> it = statements.iterator();
		while (it.hasNext()){
			ops = ops + it.next().numOps();
		}
		return ops;
	}
}

class IfNode implements ASTNode {
	ConditionExprNode condition;
	BlockNode block;
	IfNode(ConditionExprNode c, BlockNode b){
		this.condition = c;
		this.block = b;
	}
	
	int numOps(){
		return condition.numOps() + block.numOps();
	}
}

class WhileNode implements ASTNode {
	int type = 2;
	ConditionExprNode condition;
	BlockNode block;
	WhileNode(ConditionExprNode c, BlockNode b){
		this.condition = c;
		this.block = b;
	}
	int numOps(){
		return condition.numOps() + block.numOps();
	}
}

class ReturnNode implements ASTNode {
	int type = 3;
	ExpressionNode expression;
	
	ReturnNode(ExpressionNode e){
		this.expression = e;
	}
	int numOps(){
		if (expression != null){
		return expression.numOps();
		}
		else{
			return 0;
		}
	}
}

class ContinueNode implements ASTNode {
	int type = 4;
	ContinueNode(){
		
	}
}

class BreakNode implements ASTNode {
	int type = 5;
	BreakNode(){
		
	}
}

class ReadNode implements ASTNode {
	int type = 6;
	String value;
	
	ReadNode(String s){
		this.value = s;
	}
}

class WriteNode implements ASTNode {
	int type = 7;
	ExpressionNode expression;
	
	WriteNode(ExpressionNode e){
		this.expression = e;
	}
	
	int numOps(){
		return expression.numOps();
	}
}

class PrintNode implements ASTNode {
	int type = 8;
	String string;
	
	PrintNode(String s){
		this.string = s;
	}
}

class AssignmentNode implements ASTNode {
	int type = 9;
	String id;
	ExpressionNode expressionA;
	ExpressionNode expressionB;
	
	AssignmentNode(ExpressionNode e){
		this.expressionA = e;
		this.expressionB = null;
	}
	
	AssignmentNode(ExpressionNode e1, ExpressionNode e2){
		this.expressionA = e1;
		this.expressionB = e2;
	}
	
	public int numOps(){
		if(expressionB != null){
		return expressionA.numOps() + expressionB.numOps();
		}
		else{
			return expressionA.numOps();
		}
	}
}

class FuncCallNode implements ASTNode {
	int type = 10;
	String id;
	ArrayList<ExpressionNode> expression;
	
	FuncCallNode(ArrayList<ExpressionNode> e){
		this.expression = e;
	}
	
	int numOps(){
		int ops = 0;
		Iterator<ExpressionNode> it = expression.iterator();
		while (it.hasNext()){
			ExpressionNode t = it.next();
			ops = ops + t.numOps();
		}
		return ops;
	}
}

class ConditionExprNode implements ASTNode {
	ConditionNode condition1;
	ConditionNode condition2;
	TokenNames op;
	
	ConditionExprNode(TokenNames t, ConditionNode c){
		this.op = t;
		this.condition2 = c;
	}
	
	ConditionExprNode(ConditionNode c){
		this.condition1 = c;
	}
	
	public int numOps(){
		if (condition2 != null){
		return condition1.numOps() + condition2.numOps();
		}
		else{
			return condition1.numOps();
		}
	}
}

class ConditionNode implements ASTNode {
	ExpressionNode expression1;
	ExpressionNode expression2;
	TokenNames op;
	
	ConditionNode(ExpressionNode e1, TokenNames t, ExpressionNode e2){
		this.expression1 = e1;
		this.op = t;
		this.expression2 = e2;
	}
	
	public int numOps(){
		return expression1.numOps() + expression2.numOps();
	}
}

class ExpressionNode implements ASTNode {
	ExpressionNode Left;
	TokenNames addop = TokenNames.None;
	TermNode Right;
	
	ExpressionNode(){
		
	}
	
	public int numOps(){
		int leftops = 0;
		int rightops = 0;
		int addops = 0;
		if (addop != TokenNames.None){
			addops = 1;
		}
		if (Left != null){
			leftops = Left.numOps();
		}
		if (Right != null){
			rightops = Right.numOps();
		}
		return leftops + rightops + addops;
	}
}

class TermNode implements ASTNode {
	TermNode Left = null;
	TokenNames mulop = TokenNames.None;
	FactorNode Right = null;
	
	public int numOps(){
		int leftops = 0;
		int rightops = 0;
		int mulops = 0;
		if (mulop != TokenNames.None){
			mulops = 1;
		}
		if (Left != null){
			leftops = Left.numOps();
		}
		if (Right != null){
			rightops = Right.numOps();
		}
		return leftops + rightops + mulops;
	}
}

class FactorNode implements ASTNode {
	boolean fn = false;
	boolean negative = false;
	TokenNames typeval = TokenNames.None;
	String value = "";
	ExpressionNode expr;
	ArrayList<ExpressionNode> expressions;
	
	FactorNode(ExpressionNode e){
		this.expr = e;
	}
	
	FactorNode(TokenNames t, String s, boolean b){
		this.typeval = t;
		this.value = s;
		this.negative = b;
	}
	
	FactorNode(TokenNames t, String s){
		this.typeval = t;
		this.value = s;
	}
	
	FactorNode(ArrayList<ExpressionNode> e){
		this.expressions = e;
	}
	
	public int numOps(){
		if (expr != null){
			return expr.numOps();
		}
		else if (expressions != null){
			int temp = 0;
			Iterator<ExpressionNode> it = expressions.iterator();
			while (it.hasNext()){
				temp += it.next().numOps();
			}
			return temp + 1;
		}
		else if (fn == true){
			return 1;
		}
		else{
			return 0;
		}
	}
}
