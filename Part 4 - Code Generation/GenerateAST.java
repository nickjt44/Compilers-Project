import java.util.*;

/* Code written by Nick Taverner */
/* This class is modified from Danny Reinheimer's RecursiveParsing class to build the abstract syntax tree, the routine names are the same*/
public class GenerateAST {
	
	private static int numVariables;  // Keeps track of the number of variables 
	private static int numFunctions;  // Keeps track of the number of functions
	private static int numStatements; // Keeps track of the number of statements
	private static Vector<Pair<TokenNames, String>> inputTokens; // Stores the set of input tokens 
	private static Pair<TokenNames, String> currentToken = new Pair<TokenNames, String>(TokenNames.None, "");  // shows what the current token removed from the stack was for debug purposes 
	
	
	public GenerateAST(Vector<Pair<TokenNames, String>> inputTokens1) {
		numFunctions = 0;
		numVariables = 0;
		numStatements = 0;
		inputTokens = inputTokens1;
	}
	
	public ProgramNode parse() {
		ProgramNode p = program();
		if(inputTokens.firstElement().getKey() == TokenNames.eof) {
			System.out.println("Pass variable " + numVariables + " function " + numFunctions + " statement " + numStatements);
			return p;
		}
		else {
			System.out.println("error");
			return null;
		}
	}
	
	private ProgramNode program() {
		// check if we are at the eof
		if(inputTokens.firstElement().getKey() == TokenNames.eof) {
			return null;
		}
		else {
			TokenNames t = type_name();
			currentToken = inputTokens.remove(0); // get the ID token
			String ty = currentToken.getValue();
			ArrayList<IDListNode> p = data_decls(t,ty);
			ArrayList<FuncNode> f = func_list(t,ty);
			return new ProgramNode(p,f);
		}
	}
	
	private ArrayList<FuncNode> func_list(TokenNames t, String ty) {
		if(inputTokens.firstElement().getKey() == TokenNames.left_parenthesis) {
		currentToken = inputTokens.remove(0);
		ParamListNode p = parameter_list();
		currentToken = inputTokens.remove(0);
		TempFuncNode te = func_Z();
		FuncNode f = new FuncNode(t, ty, p, te.data, te.statements);
		ArrayList<FuncNode> fz = func_list_Z();
		if (fz != null){
		fz.add(0,f);
		return fz;
		}
		else{
			ArrayList<FuncNode> fy = new ArrayList<FuncNode>();
			fy.add(0,f);
			return fy;
			}
		}
		else{
			return null;
		}
	}
	
	private TempFuncNode func_Z() {
		// checks if the next token is a semicolon
		if(inputTokens.firstElement().getKey() == TokenNames.semicolon) {
			currentToken = inputTokens.remove(0); // remove the token from the stack
			return null;
		}
		
		else { 
		
			currentToken = inputTokens.remove(0);
			ArrayList<IDListNode> d = data_decls_Z();
			ArrayList<StatementNode> s = statements();
			currentToken = inputTokens.remove(0);
			// Count the number of function definitions
			numFunctions += 1;
			return new TempFuncNode(d,s);
		}
	}
	
	private ArrayList<FuncNode> func_list_Z() {
		if ((inputTokens.firstElement().getKey() == TokenNames.Void) || (inputTokens.firstElement().getKey() == TokenNames.Int) || (inputTokens.firstElement().getKey() == TokenNames.binary) || (inputTokens.firstElement().getKey() == TokenNames.decimal)) {
			TokenNames t = type_name();
				currentToken = inputTokens.remove(0);
				String ty = currentToken.getValue();
				currentToken = inputTokens.remove(0);
				ParamListNode p = parameter_list();
				currentToken = inputTokens.remove(0);
				
				TempFuncNode f = func_Z();
				FuncNode fz = new FuncNode(t,ty,p,f.data,f.statements);
				ArrayList<FuncNode> a = func_list_Z();
				if (a != null){
				a.add(0,fz);
				return a;
				}
				else {
					ArrayList<FuncNode> a1 = new ArrayList<FuncNode>();
					a1.add(0,fz);
					return a1;
				}
			}
		// return true for the empty rule
		return null;		
	}
	
	private TokenNames type_name() {
		currentToken = inputTokens.remove(0);
		return currentToken.getKey();	
	}
	
	private ParamListNode parameter_list() {
		// void <parameter list Z>
		if(inputTokens.firstElement().getKey() == TokenNames.Void) {
			currentToken = inputTokens.remove(0);
			return new ParamListNode(); //function is void
			//ArrayList<ParamNode> = parameter_list_Z();
		}
		// <non-empty list>
		else if (inputTokens.firstElement().getKey() == TokenNames.Int || inputTokens.firstElement().getKey() == TokenNames.binary || inputTokens.firstElement().getKey() == TokenNames.decimal) {
			ArrayList<ParamNode> p = non_empty_list();
			return new ParamListNode(p);
		}

		// empty
		return null;
	}
	
	
	/*private ArrayList<ParamNode> parameter_list_Z() {
		if(inputTokens.firstElement().getKey() == TokenNames.ID) {
			currentToken = inputTokens.remove(0);
			return non_empty_list_prime();
		}
		return true;
	}*/
	
	
	private ArrayList<ParamNode> non_empty_list() {
		// check for int, binary, decimal
			currentToken = inputTokens.remove(0);
			TokenNames t = currentToken.getKey();
			currentToken = inputTokens.remove(0);
			String s = currentToken.getValue();
			ArrayList<ParamNode> p = non_empty_list_prime();
			if (p != null){
			p.add(0,new ParamNode(t,s));
			return p;
			}
			else {
				ArrayList<ParamNode> p1 = new ArrayList<ParamNode>();
				p1.add(0,new ParamNode(t,s));
				return p1;
			}
	}
	
	
	private ArrayList<ParamNode> non_empty_list_prime() {
		if(inputTokens.firstElement().getKey() == TokenNames.comma) {
			currentToken = inputTokens.remove(0);
			TokenNames t = type_name();
			currentToken = inputTokens.remove(0);
			String ty = currentToken.getValue(); //ID
			ArrayList<ParamNode> a = non_empty_list_prime();
			ParamNode p = new ParamNode(t,ty);
			a.add(0,p);
			return a;
		}
		return null;
	}
	
	
	private ArrayList<IDListNode> data_decls(TokenNames t, String ty) {
		if (inputTokens.firstElement().getKey() == TokenNames.left_bracket){
		IDListNode a = id_list_Z();
		a.list.get(0).name = ty;
		a.typeval = t;
		if(a.list != null){
			
			if(inputTokens.firstElement().getKey() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0);
				// count variable 
				numVariables += 1;
				ArrayList<IDListNode> p = data_decls_Z(); //data_decls_Z();
				return p;
			}
		}
		else{
		IDListNode b = id_list_prime();
		b.list.add(0,new IDNode(ty));
		b.typeval = t;
		
			if(inputTokens.firstElement().getKey() == TokenNames.semicolon) {
				currentToken = inputTokens.remove(0);
				// since we consume the first id before we get here count this as a variable
				numVariables += 1;
				ArrayList<IDListNode> p = data_decls_Z(); //data_decls_Z();
				return p;
			}
			//return false;
		}
		}
		return null;
	}
	
	
	private ArrayList<IDListNode> data_decls_Z() {
		if (inputTokens.firstElement().getKey() == TokenNames.Int || inputTokens.firstElement().getKey() == TokenNames.Void ||inputTokens.firstElement().getKey() == TokenNames.Break ||inputTokens.firstElement().getKey() == TokenNames.Continue){
			TokenNames t = type_name();
			IDListNode id = id_list();
			id.typeval = t;
			currentToken = inputTokens.remove(0);
			ArrayList<IDListNode> x = data_decls_Z();
			if (x != null){
			x.add(0,id);
			return x;
			}
			else{
				ArrayList<IDListNode> x2 = new ArrayList<IDListNode>();
				x2.add(0,id);
				return x2;
			}
		}
		return null;
	}
	
	
	private IDListNode id_list() {
		IDNode idn = id(); 
		IDListNode ida = id_list_prime();
		if (ida != null){
		ida.list.add(0,idn);
		return ida;
		}
		else {
			IDListNode ida1 = new IDListNode();
			ida1.list.add(0,idn);
			return ida1;
		}
	}
	
	
	private IDListNode id_list_Z() {
			currentToken = inputTokens.remove(0);
				currentToken = inputTokens.remove(0);
				String name = currentToken.getValue(); //NUMBER
				currentToken = inputTokens.remove(0);
				IDListNode idl = id_list_prime();
				if (idl != null){
					idl.list.add(0,new IDNode(Integer.valueOf(name)));
					return idl;
				}
				else{
					IDListNode idl2 = new IDListNode();
					idl2.list.add(0,new IDNode(Integer.valueOf(name)));
					return idl2;
				}
	}
	
	
	private IDListNode id_list_prime() {
		if(inputTokens.firstElement().getKey() == TokenNames.comma) {
			currentToken = inputTokens.remove(0);
			IDNode idn = id();
			IDListNode idl = id_list_prime();
			if (idl != null){
			idl.list.add(0,idn);
			return idl;
			}
			else{
				IDListNode idl2 = new IDListNode();
				idl2.list.add(0,idn);
				return idl2;
			}
			}
			return null;
	}
	
	
	private IDNode id() {
			currentToken = inputTokens.remove(0);
			String idd = currentToken.getValue();
			IDNode idn = id_Z(idd);
			return idn;
	}
	
	
	private IDNode id_Z(String idd) {
		if(inputTokens.firstElement().getKey() == TokenNames.left_bracket) {
			currentToken = inputTokens.remove(0);
			if(inputTokens.firstElement().getKey() == TokenNames.NUMBER) {
				currentToken = inputTokens.remove(0);
				String num = currentToken.getValue();
				if(inputTokens.firstElement().getKey() == TokenNames.right_bracket) {
					currentToken = inputTokens.remove(0);
					// count the number of variables 
					numVariables += 1;
					IDNode n = new IDNode(idd, Integer.valueOf(num)); //Array ID element
				}
			}
		}
		// count the number of variables 
		numVariables += 1;
		IDNode n = new IDNode(idd);
		return n;
	}
	
	
	private BlockNode block_statements() {
		if(inputTokens.firstElement().getKey() == TokenNames.left_brace) {
			currentToken = inputTokens.remove(0);
			ArrayList<StatementNode> s = statements();
				if(inputTokens.firstElement().getKey() == TokenNames.right_brace) {
					currentToken = inputTokens.remove(0);
					BlockNode b = new BlockNode(s);
					return b;
				}
			}
		return null;
	}
	
	
	private ArrayList<StatementNode> statements() {
		if (inputTokens.firstElement().getKey() == TokenNames.ID || inputTokens.firstElement().getKey() == TokenNames.If || inputTokens.firstElement().getKey() == TokenNames.While ||
				inputTokens.firstElement().getKey() == TokenNames.Continue || inputTokens.firstElement().getKey() == TokenNames.Break ||inputTokens.firstElement().getKey() == TokenNames.Return ||
						inputTokens.firstElement().getKey() == TokenNames.read || inputTokens.firstElement().getKey() == TokenNames.write || inputTokens.firstElement().getKey() == TokenNames.print){
			StatementNode s = statement();
		numStatements += 1;
		ArrayList<StatementNode> sl = statements();
		if (sl != null){
			sl.add(0,s);
			return sl;
		}
		else{   
			ArrayList<StatementNode> sw = new ArrayList<StatementNode>();
			sw.add(0,s);
			return sw;
		}
		}
		return null;
	}
	
	private StatementNode statement() {
		if(inputTokens.firstElement().getKey() == TokenNames.ID) {
			currentToken = inputTokens.remove(0);
			String st = currentToken.getValue();
			StatementNode s = statement_Z();
			if (s.type == 9){
				s.assignmentnode.id = st;
			}
			else {
				s.funccallnode.id = st;
			}
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.If){
			IfNode node = if_statement();
			StatementNode s = new StatementNode(1);
			s.ifnode = node;
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.While){
			WhileNode node = while_statement();
			StatementNode s = new StatementNode(2);
			s.whilenode = node;
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.Return){
			ReturnNode node = return_statement();
			StatementNode s = new StatementNode(3);
			s.returnnode = node;
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.Break){
			BreakNode node = break_statement();
			StatementNode s = new StatementNode(4);
			s.breaknode = node;
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.Continue){
			ContinueNode node = continue_statement();
			StatementNode s = new StatementNode(5);
			s.continuenode = node;
			return s;
		}
		if(inputTokens.firstElement().getKey() == TokenNames.read) {
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			String tk = currentToken.getValue();
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			ReadNode r = new ReadNode(tk);
			StatementNode s = new StatementNode(6);
			s.readnode = r;
			return s;
		}
		
		// write left_parenthesis <expression> right_parenthesis semicolon
		if(inputTokens.firstElement().getKey() == TokenNames.write) {
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			ExpressionNode ex = expression();
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			WriteNode w = new WriteNode(ex);
			StatementNode s = new StatementNode(7);
			s.writenode = w;
			return s;
		}
		// print left_parenthesis  STRING right_parenthesis semicolon
		if(inputTokens.firstElement().getKey() == TokenNames.print) {
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			String t = currentToken.getValue();
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			PrintNode p = new PrintNode(t);
			StatementNode s = new StatementNode(8);
			s.printnode = p;
			return s;
		}
		return null;
	}
	
	
	private StatementNode statement_Z() {
		if(inputTokens.firstElement().getKey() == TokenNames.equal_sign) {
			AssignmentNode a = assignment_Z();
			StatementNode s = new StatementNode(9);
			s.assignmentnode = a;
			return s;
		}
		else {
			FuncCallNode f = func_call();
			StatementNode s = new StatementNode(10);
			s.funccallnode = f;
			return s;
		}
	}
	
	
	private AssignmentNode assignment_Z() {
		if(inputTokens.firstElement().getKey() == TokenNames.equal_sign) {
			currentToken = inputTokens.remove(0);
			ExpressionNode e = expression();
					currentToken = inputTokens.remove(0);
					return new AssignmentNode(e);
			}
		else {
			currentToken = inputTokens.remove(0);
			ExpressionNode e1 = expression();
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			ExpressionNode e2 = expression();
			currentToken = inputTokens.remove(0);
			return new AssignmentNode(e1,e2);
			}
	}
	
	
	private FuncCallNode func_call() {
		currentToken = inputTokens.remove(0);
		ArrayList<ExpressionNode> e = expr_list();
		currentToken = inputTokens.remove(0);
		currentToken = inputTokens.remove(0);
		return new FuncCallNode(e);
	}		
	
	
	private ArrayList<ExpressionNode> expr_list() {
		if(inputTokens.firstElement().getKey() == TokenNames.ID || inputTokens.firstElement().getKey() == TokenNames.NUMBER 
				|| inputTokens.firstElement().getKey() == TokenNames.minus_sign || inputTokens.firstElement().getKey() == TokenNames.left_parenthesis) {
			return non_empty_expr_list();
		}
		return null;
	}
	
	
	private ArrayList<ExpressionNode> non_empty_expr_list() {
		ExpressionNode e = expression();
		ArrayList<ExpressionNode> en = non_empty_expr_list_prime();
		if (en != null){
		en.add(0,e);
		return en;
		}
		else{
			ArrayList<ExpressionNode> ex = new ArrayList<ExpressionNode>();
			ex.add(0,e);
			return ex;
		}
	}
	
	
	private ArrayList<ExpressionNode> non_empty_expr_list_prime() {
		if(inputTokens.firstElement().getKey() == TokenNames.comma) {
			currentToken = inputTokens.remove(0);
			ExpressionNode ex = expression();
			ArrayList<ExpressionNode> en = non_empty_expr_list_prime();
			en.add(0,ex);
			return en;
			}
		return null;
	}
	
	
	private IfNode if_statement() {
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			ConditionExprNode c = condition_expression();
			currentToken = inputTokens.remove(0);
			BlockNode b = block_statements();
			return new IfNode(c,b);
		}
	
	private ConditionExprNode condition_expression() {
		ConditionNode c = condition();
		ConditionExprNode ce = condition_expression_Z();
		if (ce != null){
			ce.condition1 = c;
			return ce;
		}
		else{
			ConditionExprNode c2 = new ConditionExprNode(c);
			return c2;
		}
		}
	
	
	private ConditionExprNode condition_expression_Z() {
		if (inputTokens.firstElement().getKey() == TokenNames.double_and_sign || inputTokens.firstElement().getKey() == TokenNames.double_or_sign){
		TokenNames t = condition_op();
		ConditionNode c = condition();
		ConditionExprNode ce = new ConditionExprNode(t,c);
		return ce;
		}
		return null;
	}
	
	
	private TokenNames condition_op() {
			currentToken = inputTokens.remove(0);
			return currentToken.getKey();
	}
	
	
	private ConditionNode condition() {
		ExpressionNode e1 = expression();
		TokenNames t = comparison_op();
		ExpressionNode e2 = expression();
		return new ConditionNode(e1,t,e2);
	}
	
	
	private TokenNames comparison_op() {
			currentToken = inputTokens.remove(0);
			return currentToken.getKey();
		}
	
	
	private WhileNode while_statement() {
		currentToken = inputTokens.remove(0);
		currentToken = inputTokens.remove(0);
		ConditionExprNode c = condition_expression();
		currentToken = inputTokens.remove(0);
		BlockNode b = block_statements();
		return new WhileNode(c,b);
	}
	
	
	private ReturnNode return_statement() {
			currentToken = inputTokens.remove(0);
			ExpressionNode e = return_statement_Z();
			return new ReturnNode(e);
	}
	
	
	private ExpressionNode return_statement_Z() {
		if(inputTokens.firstElement().getKey() == TokenNames.semicolon) {
			currentToken = inputTokens.remove(0);
			return null;
		}
		else{
		ExpressionNode e = expression();
				currentToken = inputTokens.remove(0);
				return e;
		}
	}
	
	
	private BreakNode break_statement() {
			currentToken = inputTokens.remove(0);
				currentToken = inputTokens.remove(0);
				return new BreakNode();
	}
	
	
	private ContinueNode continue_statement() {
			currentToken = inputTokens.remove(0);
				currentToken = inputTokens.remove(0);
				return new ContinueNode();
	}
	
	
	private ExpressionNode expression() {
		TermNode t = term();
		ExpressionNode e = expression_prime();
		if (e != null){
		e.Right = t;
		return e;
		}
		else{
			ExpressionNode e1 = new ExpressionNode();
			e1.Right = t;
			return e1;
		}
	}
	
	
	private ExpressionNode expression_prime() {
		if (inputTokens.firstElement().getKey() == TokenNames.plus_sign || inputTokens.firstElement().getKey() == TokenNames.minus_sign){
		TokenNames a = addop();
		TermNode t = term();
		ExpressionNode e = expression_prime();
		if (e != null){
			e.Right = t;
			ExpressionNode ex = new ExpressionNode();
			ex.addop = a;
			ex.Left = e;
			return ex;
		}
		else{
			ExpressionNode ey = new ExpressionNode();
			ey.Right = t;
			ExpressionNode ex = new ExpressionNode();
			ex.addop = a;
			ex.Left = ey;
			return ex;
			}
		}
		return null;
	}
	
	
	private TokenNames addop() {
			currentToken = inputTokens.remove(0);
			return currentToken.getKey();
	}
	
	
	private TermNode term() {
		FactorNode f = factor();
		TermNode t = term_prime();
		if (t != null){
			t.Right = f;
			return t;
		}
		else{
			TermNode t1 = new TermNode();
			t1.Right = f;
			return t1;
		}
		
	}
	
	
	private TermNode term_prime() {
		if(inputTokens.firstElement().getKey() == TokenNames.star_sign || inputTokens.firstElement().getKey() == TokenNames.forward_slash){
		TokenNames o = mulop();
		FactorNode f = factor();
		TermNode t = term_prime();
		if(t != null){
			TermNode tn = new TermNode();
			t.Right = f;
			tn.Left = t;
			tn.mulop = o;
			return tn;
		}
		else{
			TermNode t2 = new TermNode();
			TermNode t1 = new TermNode();
			t2.Right = f;
			t1.Left = t2;
			t1.mulop = o;
			return t1;
			}
		}
		return null;
	}
	
	
	private TokenNames mulop() {
			currentToken = inputTokens.remove(0);
			return currentToken.getKey();
	}
	
	
	private FactorNode factor() {
		if(inputTokens.firstElement().getKey() == TokenNames.ID) {
			currentToken = inputTokens.remove(0);
			TokenNames t = currentToken.getKey();
			String v = currentToken.getValue();
			FactorNode f = factor_Z();
			if (f != null){
			f.typeval = t;
			f.value = v;
			return f;
			}
			else{
				FactorNode fn = new FactorNode(t, v);
				return fn;
			}
		}
		// NUMBER
		if(inputTokens.firstElement().getKey() == TokenNames.NUMBER) {
			currentToken = inputTokens.remove(0);
			FactorNode f = new FactorNode(currentToken.getKey(),currentToken.getValue(), false);
			return f;
		}
		
		// minus_sign NUMBER
		if(inputTokens.firstElement().getKey() == TokenNames.minus_sign) {
			currentToken = inputTokens.remove(0);
			currentToken = inputTokens.remove(0);
			FactorNode f = new FactorNode(currentToken.getKey(),currentToken.getValue(), true);
			return f;
		}
		
		// left_parenthesis <expression>right_parenthesis
		else {
			currentToken = inputTokens.remove(0);
			ExpressionNode e = expression();
			currentToken = inputTokens.remove(0);
			FactorNode f = new FactorNode(e);
			return f;
			}
	}
	
	
   private FactorNode factor_Z() {
		// left_bracket <expression> right_bracket
		if(inputTokens.firstElement().getKey() == TokenNames.left_bracket) {
			currentToken = inputTokens.remove(0);
			ExpressionNode e = expression();
				if(inputTokens.firstElement().getKey() == TokenNames.right_bracket) {
					currentToken = inputTokens.remove(0);
					FactorNode f = new FactorNode(e);
					return f;
				}
			}
		// left_parenthesis <expr list> right_parenthesis
		if(inputTokens.firstElement().getKey() == TokenNames.left_parenthesis) {
			currentToken = inputTokens.remove(0);
			ArrayList<ExpressionNode> a = expr_list();
				if(inputTokens.firstElement().getKey() == TokenNames.right_parenthesis) {
					currentToken = inputTokens.remove(0);
					FactorNode f = new FactorNode(a);
					f.fn = true;
					return f;
				}
			}
		// empty
		return null;
	} 
	

}
