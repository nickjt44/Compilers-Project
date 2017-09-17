/*Code for recursive code generation alrogithm from abstract syntax tree*/
/* Code written by Nick Taverner */

import java.lang.*;
import java.io.*;
import java.util.*;
import java.lang.Math;

//The only class I altered for Lab 4
public class CodeGenerator2 {
	String outputString = "";
	int functionCount = -1;
	ArrayList<Integer> arraycounts = new ArrayList<Integer>();
	int currentWhileStart; //beginning of current while loop for break/continue statements
	int currentWhileEnd; //end of current while loop for break/continue statements
	int gotoval = 0;
	int currentFuncStart = 0; //start of current function
	int indexCount = 0; //counts array index for each function
	int varCount = 0;
	int exprCount = 0;
	int labelCount = 1;
	String currentFunction = ""; //current function name
	String callingFunction = "";
	int currentFuncIndex = -1;
	boolean tempvoid = false;
	ArrayList<FuncNode> FunctionArray;
	ArrayList<String> UsedFunctions = new ArrayList<String>(); //arraylist representing which functions have been called by other functions in the program
	
	ArrayList<FuncArray> funcs = new ArrayList<FuncArray>();
	
	CodeGenerator2(int num){
		//numfunctions = num;
		//for (int i = 0; i < 2000; i++){
			//vars.add(null);
		//}
	}
	
	//generates the output program
	public String generateProgram(ProgramNode p){
		if (p == null){
			return outputString;
		}
		else{
			if (p.data != null){
				outputString = outputString + "int ";
				ArrayList<IDListNode> id = p.data;
				generateDataDecls(id, false);
				
			}
			FunctionArray = p.functions;
			Iterator<FuncNode> it = FunctionArray.iterator();
			while (it.hasNext()){
				FuncNode f = it.next();
				funcs.add(new FuncArray(f.name));
			}
			
			it = FunctionArray.iterator();
			while (it.hasNext()){
				FuncNode f = it.next();
				if (f.name.equals("main")){
					generateMain(f);
				}
			}
			
			it = FunctionArray.iterator();
			while (it.hasNext()){
				FuncNode f = it.next();
				if (f.name.equals("main")){
					continue;
				}
				else{
					generateFunction(f);
				}
			}
			//generateFunction(p.functions.get(0));
			System.out.println(outputString); /////
			
			outputString = outputString + "jumpTable:\n switch(jumpReg) {\n case 0: exit(0);\n";
			for (int i = 1; i < FunctionArray.size(); i++){
				outputString = outputString + " case " + i + ":\n";
				outputString = outputString + "goto label_" + i + ";\n";
			}
			outputString = outputString + "default: assert(0);\n" + "}\n" + "}\n";
			return outputString;
			}
		}
	
	//generates data declarations
	public void generateDataDecls(ArrayList<IDListNode> idlist, boolean global){
		if (idlist != null){
			Iterator<IDListNode> it = idlist.iterator();
			while (it.hasNext()){
				IDListNode id = it.next();
				generateIDList(id, global);
			}
		}
	}
	
	//generates an IDlist
	public void generateIDList(IDListNode idlist, boolean global){
		int tempIndex = -1;
		Iterator<FuncArray> itf = funcs.iterator();
		while (itf.hasNext()){
			FuncArray fn = itf.next();
			if (fn.name.equals(currentFunction)){
				tempIndex = funcs.indexOf(fn);
				break;
			}
		}
		System.out.println("tempIndex is " + tempIndex);
		
		Iterator<IDNode> it = idlist.list.iterator();
		while (it.hasNext()){
			IDNode id = it.next();
			//if(id.number == null){
			funcs.get(tempIndex).vars.add(id.name);
			indexCount++;
			varCount++;
		}
		System.out.println("indexCount is" + indexCount);
	}
		
	//generates the main function for the program
	public void generateMain(FuncNode f){
		indexCount = 0;
		currentFunction = "main";
		Iterator<FuncArray> itf = funcs.iterator();
		while(itf.hasNext()){
			FuncArray fna = itf.next();
			if (fna.name.equals("main")){
				currentFuncIndex = funcs.indexOf(fna);
			}
		}
		
		outputString = outputString + "int main() {\n\n";
		outputString = outputString + "top = membase;\n";
		outputString = outputString + "mem[top] = 0;\n";
		outputString = outputString + "base = top + 1;\n";
		
		ArrayList<StatementNode> s = f.statements;
		
		int variables = 0;
		if (f.data != null){
		Iterator<IDListNode> it = f.data.iterator();
		while (it.hasNext()){
			IDListNode idn = it.next();
			variables = variables + idn.list.size();
			}
		}
		System.out.println(f.numOps());
		
		outputString = outputString + "top = base + " + (variables + f.numOps()) + ";\n\n";
		generateDataDecls(f.data, false);
		outputString = outputString + "mainFunc:\n";
		generateStatements(s);
		outputString = outputString + "jumpReg = mem[base-1];\n" + "goto jumpTable;\n\n";
	}
	
	//generates a function
	public void generateFunction(FuncNode f){
		currentFunction = f.name;
		functionCount += 1;
		indexCount = 0;
		Iterator<FuncArray> itf = funcs.iterator();
		while(itf.hasNext()){
			FuncArray fna = itf.next();
			if (fna.name.equals(f.name)){
				currentFuncIndex = funcs.indexOf(fna);
			}
		}
		
		//vars = new ArrayList<DoubleVals>(); //arraylist to compare old variables with array values in new program
		
		ArrayList<StatementNode> s = f.statements;
		ParamListNode p = f.params;
		
		int variables = 0;
		if (f.data != null){
		Iterator<IDListNode> it = f.data.iterator();
		while (it.hasNext()){
			IDListNode idn = it.next();
			variables = variables + idn.list.size();
			}
		}
		System.out.println(f.numOps());
		
		generateDataDecls(f.data, false);
		outputString = outputString + f.name + "Func:\n";
		generateStatements(s);
		//outputString = outputString + "top = mem[base-3];\n" + "jumpReg = mem[base-1];\n" + "base = mem[base-4];\n" + "goto jumpTable;\n\n";
		
	}
	
	//generates new list of statements, using switch statement to determine which specific one
	public void generateStatements(ArrayList<StatementNode> s){
	Iterator<StatementNode> it1 = s.iterator(); //testing
	while (it1.hasNext()){
		StatementNode x = it1.next();
	}
		
	Iterator<StatementNode> it = s.iterator();
	while (it.hasNext()){
		StatementNode st = it.next();
		switch (st.type){
			case 0:
				break;
			case 1: //if
				generateIf(st.ifnode);
				break;
			case 2: //while
				generateWhile(st.whilenode);
				break;
			case 3: //return
				generateReturn(st.returnnode);
				break;
			case 4: //continue
				generateBreak();
				break;
			case 5: //break
				generateContinue();
				break;
			case 6: //read
				generateRead(st.readnode);
				break;
			case 7: //write
				generateWrite(st.writenode);
				break;
			case 8: //print
				generatePrint(st.printnode);
				break;
			case 9: //assignment
				generateAssignment(st.assignmentnode);
				break;
			case 10: //func call
				generateFuncCall(st.funccallnode);
				break;
				
			}
		}
	}
	
	//generates a new if statement using gotos
	public void generateIf(IfNode ifnode){
		String s = generateCondExpr(ifnode.condition);
		outputString = outputString + s + " goto " + "c" + Integer.toString(gotoval) + ";\n";
		gotoval = gotoval +1;
		outputString = outputString + "goto " + "c" + Integer.toString(gotoval) + ";\n";
		gotoval = gotoval + 1;
		outputString = outputString + "c" + Integer.toString(gotoval-2) + ":;\n";
		int ifgoto = gotoval-1;
		generateStatements(ifnode.block.statements);
		outputString = outputString + "c" + Integer.toString(ifgoto) + ":;\n";
		}
	
	public void generateContinue(){
		outputString = outputString + "goto " + "c" + Integer.toString(currentWhileStart) + ";\n";
	}
	
	public void generateBreak(){
		outputString = outputString + "goto " + "c" + Integer.toString(currentWhileEnd) + ";\n";
	}
	//generates a while statement in terms of ifs and gotos
	public void generateWhile(WhileNode whilenode){
		outputString = outputString + "c" + Integer.toString(gotoval) + ":;\n";
		currentWhileStart = gotoval;
		gotoval = gotoval + 1;
		String s = generateCondExpr(whilenode.condition);
		outputString = outputString + s + " goto " + "c" + Integer.toString(gotoval) + ";\n";
		gotoval = gotoval + 1;
		outputString = outputString + "goto " + "c" + Integer.toString(gotoval) + ";\n";
		gotoval = gotoval +1;
		outputString = outputString + "c" + Integer.toString(gotoval-2) + ":;\n";
		int tempgoto = gotoval-1;
		currentWhileEnd = tempgoto;
		generateStatements(whilenode.block.statements);
		outputString = outputString + "goto " + "c" + Integer.toString(currentWhileStart) + ";\n";
		outputString = outputString + "c" + Integer.toString(tempgoto) + ":;\n";
	}
	//generates a return statement
	public void generateReturn(ReturnNode r){
		if (r.expression != null){
			String s = generateExpression(r.expression);
			int array1 = indexCount-1;
			if (s == ""){
				outputString = outputString + "mem[base-2] " + "= mem[base+" + Integer.toString(array1) + "];\n";
				outputString = outputString + "top = mem[base-3];\n" + "jumpReg = mem[base-1];\n" + "base = mem[base-4];\n" + "goto jumpTable;\n\n";
			}
			else{
				outputString = outputString + "mem[base-2] = " + s + ";\n";
				outputString = outputString + "top = mem[base-3];\n" + "jumpReg = mem[base-1];\n" + "base = mem[base-4];\n" + "goto jumpTable;\n\n";
			}
		}
		else{
			outputString = outputString + "mem[base-2] = 0;\n";
			outputString = outputString + "top = mem[base-3];\n" + "jumpReg = mem[base-1];\n" + "base = mem[base-4];\n" + "goto jumpTable;\n\n";
		}
	}
	//generates a read statement
	public void generateRead(ReadNode r){
		
		outputString = outputString + "read(mem[base+" + funcs.get(currentFuncIndex).vars.indexOf(r.value) + "]);\n";
 	}
	//generates a write statement
	public void generateWrite(WriteNode w){
		String s = generateExpression(w.expression);
		if (s == ""){
			outputString = outputString + "write(mem[base+" + Integer.toString(indexCount-1) + "]);\n";
		}
		else{
			outputString = outputString + "write(" + s + ");\n";
		}
	}
	//generates a print statement
	public void generatePrint(PrintNode p){
		outputString = outputString + "print(" + p.string + ");\n";
	}
	//generates an assignment
	public void generateAssignment(AssignmentNode a){
		int count = 0;
		boolean parameter = false;
		System.out.println("indexcount is" + indexCount);
		ExpressionNode expression = a.expressionA;
		String s = generateExpression(expression);
		int arrayval = funcs.get(currentFuncIndex).vars.indexOf(a.id);
		if (arrayval == -1){
			parameter = true;
			ParamListNode p = FunctionArray.get(currentFuncIndex).params;
			Iterator<ParamNode> itp = p.params.iterator();
			while (itp.hasNext()){
				ParamNode p2 = itp.next();
				if (p2.value.equals(a.id)){
					break;
				}
				count++;
			}
		}
		if (s != "" && parameter == false){
			outputString = outputString + "mem[base+" + Integer.toString(arrayval) + "] = " + s + ";\n";
		}
		else if (s == "" && parameter == false){
			System.out.println("arrayval IS " + arrayval);
			outputString = outputString + "mem[base+" + Integer.toString(arrayval) + "] = " + "mem[base+" + Integer.toString(indexCount-1) + "];\n";
		}
		else if (s != "" && parameter == true){
			outputString = outputString + "mem[base-4-" + (FunctionArray.get(currentFuncIndex).params.params.size() - count) + "] = " + s + ";\n";
		}
		else{
			outputString = outputString + "mem[base-4-" + (FunctionArray.get(currentFuncIndex).params.params.size() - count) + "] = " + "mem[base+" + Integer.toString(indexCount-1) + "];\n";
		}
		
	}
	
	//generates a function call
	//adjusts memory locations as necessary for the function
	//if the function has not already been called, creates new goto location for the function
	public void generateFuncCall(FuncCallNode f){
		//indexCount = 0; //////
		
		
		String name = f.id;
		boolean check = false;
		FuncNode myFunc = null;
		int paramNum = 0;
		Iterator<FuncNode> it = FunctionArray.iterator();
		while (it.hasNext()){  //find function node associated with function call name
			FuncNode fd = it.next();
			if (fd.name.equals(name)){
				check = true;
				myFunc = fd;
			}
		}
		
		int variables = 0;
		if (myFunc.data != null){
		Iterator<IDListNode> it2 = myFunc.data.iterator();
		while (it2.hasNext()){
			IDListNode idn = it2.next();
			variables = variables + idn.list.size();
			}
		}
		
		ParamListNode p = myFunc.params;
		ArrayList<ExpressionNode> ex = f.expression;
		
		if (p == null){
			paramNum = 0;
		}
		else if (p.isVoid == true){
			paramNum = 0;
		}
		else{
			paramNum = p.params.size();
		}
		
		if (paramNum == 0){
			tempvoid = true;
		}
		
		for (int i = 0; i < paramNum; i++){
			String s = generateExpression(ex.get(i));
			if (s != ""){
			outputString = outputString + "mem[top+" + i + "] = " + s + ";\n";
			}
			else{
				outputString = outputString + "mem[top+" + i + "] = mem["  + indexCount + "];\n";
			}
			indexCount++;
		}
		
		outputString = outputString + "mem[top+" + paramNum + "] = base;\n";
		outputString = outputString + "mem[top+" + (paramNum+1) + "] = top;\n";
		outputString = outputString + "mem[top+" + (paramNum+3) + "] = " + labelCount + ";\n";
		outputString = outputString + "base = top + " + (paramNum + 4) + ";\n";
		
		outputString = outputString + "top = base + " + (paramNum + variables + myFunc.numOps()) + ";\n\n";
		outputString = outputString + "goto " + name + "Func;\n\n";
		
		outputString = outputString + "label_" + labelCount + ":\n";
		labelCount++;
		outputString = outputString + "mem[base+" + indexCount + "] = mem[top+" +  (paramNum+2) + "];\n";
		indexCount++;
	}
	
	
	//generates a larger condition expressions with ands or ors
	public String generateCondExpr(ConditionExprNode cond){
		String str;
		String cond1 = generateCondition(cond.condition1);
		if(cond.condition2 != null){
			String cond2 = generateCondition(cond.condition2);
			if (cond.op == TokenNames.double_and_sign){
				str =  "mem[base+" + Integer.toString(indexCount) + "] = " + cond1 + ";\n";
				indexCount += 1;
				str = str +  "mem[base+" + Integer.toString(indexCount) + "] = " + cond2 + ";\n";
				indexCount += 1;
				str = str + "if (mem[base+" + Integer.toString(indexCount - 2) + "] && mem[base+" + Integer.toString(indexCount - 1) + "]) ";
				return str;
			}
			else{
				str =  "mem[base+" + Integer.toString(indexCount) + "] = " + cond1 + ";\n";
				indexCount += 1;
				str = str +  "mem[base+" + Integer.toString(indexCount) + "] = " + cond2 + ";\n";
				indexCount += 1;
				str = str + "if (mem[base+" + Integer.toString(indexCount - 2) + "] || mem[base+" + Integer.toString(indexCount - 1) + "]) ";
				return str;
			}
			
		}
		else{
			str = "if (" + cond1 + ") "; 
			return str;
		}
	}
	//generates a condition expression from one or two expressions
	public String generateCondition(ConditionNode condition){
		String str;
		String exp1 = generateExpression(condition.expression1);
		int array1 = indexCount-1;
		String exp2 = generateExpression(condition.expression2);
		int array2 = indexCount-1;
		String op;
		
		
		if (condition.op == TokenNames.doubleEqualSign){
			op = "==";
		}
		else if (condition.op == TokenNames.greaterThenOrEqualSign){
			op = ">=";
		}
		else if (condition.op == TokenNames.lessThenOrEqualSign){
			op = "<=";
		}
		else if (condition.op == TokenNames.greaterThenSign){
			op = ">";
		}
		else if (condition.op == TokenNames.lessThenSign){
			op = "<";
		}
		else {
			op = "!=";
		}
		
		
		if(exp1 != "" && exp2 != ""){
			str = exp1 + " " + op + " " + exp2; 
		}
		else if(exp1 != "" && exp2 == ""){
			str = exp1 + " " + op + " mem[base+" + Integer.toString(array2) + "]";
		}
		else if(exp1 == "" && exp2 != ""){
			str = "mem[base+" + Integer.toString(array1) + "] " + op + " " + exp2;
		}
		else{
			str = "mem[base+" + Integer.toString(array1) + "] " + op + " mem[base+" + Integer.toString(array2) + "]";
		}
		return str;
	}
	//generates an expression from calling Left and Right children, then repeating
	//until values are returned
	public String generateExpression(ExpressionNode expression){
		if (expression.Left == null){
			String term = generateTerm(expression.Right);
			if (term == ""){
				return "";
			}
			else{
				return term;
			}
			
		}
		else{
			String op;
			if (expression.addop == TokenNames.plus_sign){
				op = "+";
			}
			else{
				op = "-";
			}
			String e = generateExpression(expression.Left);
			String t = generateTerm(expression.Right);
			if (e == "" && t == ""){
			outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + "mem[base+" + Integer.toString(indexCount-2) + "] " + op + " mem[base+" + Integer.toString(indexCount-1) + "];\n";
			}
			else if(e != "" && t == "") {
				System.out.println("XDDDDDDDDDDDDDDDDDD");
				System.out.println("indexcount is xxxxxxx" + indexCount);
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + "mem[base+" + Integer.toString(indexCount-1) + "] " + op + " " + e + ";\n";
			}
			else if(e == "" && t != "") {
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + t + op + "mem[base+" + Integer.toString(indexCount-1) + "] " + ";\n";
			}
			else{
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + t + " " + op + " " + e + ";\n";
			}
			indexCount += 1;
			return "";
		}
		
	}
	//generates term
	public String generateTerm(TermNode term){
		if (term.Left == null){
			String factor = generateFactor(term.Right);
			if (factor == ""){ //expression, not value
				return "";
			}
			else{
				return factor;
			}
		}
		else{
			String op;
			if (term.mulop == TokenNames.star_sign){
				op = "*";
			}
			else{
				op = "/";
			}
			String t = generateTerm(term.Left);
			String f = generateFactor(term.Right);
			if (t == "" && f == ""){
			outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + "mem[base+" + Integer.toString(indexCount-2) + "] " + op + " mem[base+" + Integer.toString(indexCount-1) + "];" + "\n";
			}
			else if(t != "" && f == "") {
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + "mem[base+" + Integer.toString(indexCount-1) + "] " + op + " " + t + ";" + "\n";
			}
			else if(t == "" && f != "") {
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + f + op + "mem[base+" + Integer.toString(indexCount-1) + "] " + ";" + "\n";
			}
			else{
				outputString = outputString + "mem[base+" + Integer.toString(indexCount) + "] = " + f + " " + op + " " + t + ";" + "\n";
			}
			indexCount += 1;
			return "";
		}
	}
	//generates factor
	public String generateFactor(FactorNode factor){
		if (factor.expr == null && factor.expressions == null && factor.fn == false){
			if (factor.typeval == TokenNames.NUMBER && factor.negative == false){
				return factor.value;
			}
			else if (factor.typeval == TokenNames.NUMBER && factor.negative == true){
				return "-" + factor.value;
			}
			else{
				if (funcs.get(currentFuncIndex).vars.indexOf(factor.value) != -1){
				return "mem[base+" + Integer.toString(funcs.get(currentFuncIndex).vars.indexOf(factor.value)) +"]"; //variable declared
				}
				else{
					FuncNode func = null;
					Iterator<FuncNode> it = FunctionArray.iterator();
					while(it.hasNext()){
						FuncNode fn = it.next();
						if (fn.name.equals(currentFunction)){
							func = fn;
							break;
						}
					}
					ArrayList<ParamNode> p = func.params.params;
					int count = 0;
					Iterator<ParamNode> itp = p.iterator();
					while(itp.hasNext()){
						ParamNode pn = itp.next();
						if (pn.value.equals(factor.value)){
							funcs.get(currentFuncIndex).vars.add(factor.value);
							break;
						}
						count++;
					}
					
					if (count < p.size()){
						outputString = outputString + "mem[base+" + indexCount +"] = mem[base-4-" + (p.size() - count) + "];\n";
						indexCount++;
					}
					
					/*outputString = outputString + "mem[" + indexCount +"] = " + factor.value + ";\n";
					indexCount++;
					return "mem[" + Integer.toString(indexCount-1) + "]"; //variable passed */
					return "";
				}
			}
		}
		else if (factor.expr != null){
			return generateExpression(factor.expr);
		}
		else{
				FuncCallNode fnode = new FuncCallNode(factor.expressions);
				fnode.id = factor.value;
				generateFuncCall(fnode);
				return "";
			}
		}
	}



class FuncArray {
	String name;
	ArrayList<String> vars = new ArrayList<String>();
	
	FuncArray(String n){
		name = n;
	}
}
