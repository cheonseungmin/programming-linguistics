package programing;

import java.util.*;

public class Parser {
	// Recursive descent parser that inputs a C++Lite program and
	// generates its abstract syntax. Each method corresponds to
	// a concrete syntax grammar rule, which appears as a comment
	// at the beginning of the method.

	Token token; // current token from the input stream
	Lexer lexer;

	public Parser(Lexer ts) { // Open the C++Lite source program
		lexer = ts; // as a token stream, and
		token = lexer.next(); // retrieve its first Token
	}

	private String match(TokenType t) {
		String value = token.value();
		if (token.type().equals(t))
			token = lexer.next();
		else
			error(t);
		return value;
	}

	private void error(TokenType tok) {
		System.err.println("Syntax error: expecting: " + tok + "; saw: " + token);
		System.exit(1);
	}

	private void error(String tok) {
		System.err.println("Syntax error: expecting: " + tok + "; saw: " + token);
		System.exit(1);
	}

	public Program program() {
		Declarations gs = new Declarations();
		Functions fs = new Functions();

		while (true) {
			if (token == Token.eofTok) {
				break;
			} else if (token.type() == TokenType.Int) { // int => global or function or main
				Type tmpt = type();
				match(TokenType.Int);
				if (token.type() == TokenType.Main) {
					TokenType[] header = { TokenType.Main, TokenType.LeftParen, TokenType.RightParen };
					for (int i = 0; i < header.length; i++)
						match(header[i]);
					match(TokenType.LeftBrace); // {
					// student exercise

					Declarations ds = declarations();
					Block b = statements("main");
					fs.add(new Function(Type.INT, "main", new Declarations(), ds, b));
					match(TokenType.RightBrace); // }
				} else if (token.type() == TokenType.Identifier) { // global or function
					Variable tmpv = new Variable(token.value());

					String tmpid = token.value();

					match(TokenType.Identifier);
					if (token.type() != TokenType.LeftParen) { // global
						Declaration d = new Declaration(tmpv, tmpt);
						gs.add(d);
						while (token.type().equals(TokenType.Comma)) {
							token = lexer.next();
							tmpv = new Variable(token.value());
							d = new Declaration(tmpv, tmpt);
							gs.add(d);
							token = lexer.next();
						}
						match(TokenType.Semicolon);

					} else if (token.type() == TokenType.LeftParen) { // function
						match(TokenType.LeftParen);
						Declarations ps = params();
						match(TokenType.RightParen);
						match(TokenType.LeftBrace);
						Declarations l = declarations();
						Block b = statements(tmpid);
						match(TokenType.RightBrace);
						fs.add(new Function(tmpt, tmpid, ps, l, b));
					}
				}
			} else { // not int => global or function
				Type tmpt = type();
				token = lexer.next();
				if(token.type() == TokenType.Main) {
					System.out.println("error : main은 int 앞에만 올 수 있음");
					System.exit(1);
				}
				else if (token.type() == TokenType.Identifier) { // global or function
					Variable tmpv = new Variable(token.value());

					String tmpid = token.value();

					match(TokenType.Identifier);
					if (token.type() != TokenType.LeftParen) { // global
						Declaration d = new Declaration(tmpv, tmpt);
						gs.add(d);
						while (token.type().equals(TokenType.Comma)) {
							token = lexer.next();
							tmpv = new Variable(token.value());
							d = new Declaration(tmpv, tmpt);
							gs.add(d);
							token = lexer.next();
						}
						match(TokenType.Semicolon);

					} else if (token.type() == TokenType.LeftParen) { // function
						match(TokenType.LeftParen);
						Declarations ps = params();
						match(TokenType.RightParen);
						match(TokenType.LeftBrace);
						Declarations l = declarations();
						Block b = statements(tmpid);
						match(TokenType.RightBrace);
						fs.add(new Function(tmpt, tmpid, ps, l, b));
					}
				}
			}
		}
		Program p = new Program(gs, fs);
		return p; // student exercise
	}

	private Declarations declarations() {
		// Declarations --> { Declaration }
		Declarations ds = new Declarations();
		while (isType()) {
			declaration(ds);
		}
		return ds; // student exercise
	}

	private void declaration(Declarations ds) {
		// Declaration --> Type Identifier { , Identifier } ;
		// student exercise
		Type dt = type();
		do {
			token = lexer.next();
			Variable v = new Variable(token.value());
			Declaration d = new Declaration(v, dt);
			ds.add(d);
			token = lexer.next();
		} while (token.type().equals(TokenType.Comma));
		match(TokenType.Semicolon);
	}

	private Declarations params() {
		// Declarations --> { Declaration }
		Declarations ps = new Declarations();
		while (isType()) {
			param(ps);
			if (token.type() == TokenType.Comma) {
				token = lexer.next();
				continue;
			} else if (token.type() == TokenType.RightParen) {
				break;
			}
		}
		return ps; // student exercise
	}

	private void param(Declarations ps) {
		// Declaration --> Type Identifier { , Identifier } ;
		// student exercise
		Type dt = type();
		token = lexer.next();
		Variable v = new Variable(token.value());
		Declaration p = new Declaration(v, dt);
		ps.add(p);
		token = lexer.next();
	}

	private Type type() {
		// Type --> int | bool | float | char
		Type t = null;
		switch (token.type()) {
		case Int:
			t = Type.INT;
			break;
		case Bool:
			t = Type.BOOL;
			break;
		case Float:
			t = Type.FLOAT;
			break;
		case Char:
			t = Type.CHAR;
			break;
		case Void:
			t = Type.VOID;
			break;
		}

		return t;
	}

	private Statement statement(String functionName) {
		// Statement --> ; | Block | Assignment | IfStatement | WhileStatement
		Statement s = new Skip();

		if (token.type() == TokenType.LeftBrace) {
			match(TokenType.LeftBrace);
			Block b = statements(functionName);
			match(TokenType.RightBrace);
			return b;
		} else if (token.type() == TokenType.Identifier) {
			String tmp = token.value();
			token = lexer.next();
			if (token.type() == TokenType.Assign) {
				return assignment(tmp);
			} else if (token.type() == TokenType.LeftParen) {
				return callStatement(tmp);
			}
		} else if (token.type() == TokenType.If) {
			match(TokenType.If);
			return ifStatement(functionName);
		} else if (token.type() == TokenType.While) {
			match(TokenType.While);
			return whileStatement(functionName);
		} else if (token.type() == TokenType.Put) {
			match(TokenType.Put);
			return putStatement();
		} else if (token.type() == TokenType.Return) {
			match(TokenType.Return);
			Expression e = expression();
			return ReturnStatement(functionName, e);
		}

		// student exercise
		return s;
	}

	private Block statements(String functionName) {
		// Block --> '{' Statements '}'
		Block b = new Block();

		// student exercise
		while (token.type() != TokenType.RightBrace && token.type() != TokenType.Eof)
			b.members.add(statement(functionName));

		return b;
	}

	private Assignment assignment(String tmp) {
		// Assignment --> Identifier = Expression ;
		Variable v = new Variable(tmp);
		match(TokenType.Assign);
		Expression e = expression();
		Assignment a = new Assignment(v, e);
		match(TokenType.Semicolon);

		return a; // student exercise
	}

	private Conditional ifStatement(String functionName) {
		// IfStatement --> if ( Expression ) Statement [ else Statement ]
		match(TokenType.LeftParen);
		Expression e = expression();
		match(TokenType.RightParen);
		Statement s1 = statement(functionName);
		Conditional c = null;
		if (token.type().equals(TokenType.Else)) {
			match(TokenType.Else);
			Statement s2 = statement(functionName);
			c = new Conditional(e, s1, s2);
		} else
			c = new Conditional(e, s1);
		return c; // student exercise
	}

	private Loop whileStatement(String functionName) {
		// WhileStatement --> while ( Expression ) Statement
		match(TokenType.LeftParen);
		Expression e = expression();
		match(TokenType.RightParen);
		Statement s1 = statement(functionName);
		Loop l = new Loop(e, s1);
		return l; // student exercise
	}

	private Put putStatement() {
		// putStatement --> put(Expression);
		match(TokenType.LeftParen);

		Expression e = expression();

		match(TokenType.RightParen);
		match(TokenType.Semicolon);

		Put p = new Put(e);
		return p;
	}

	private Return ReturnStatement(String functionName, Expression r) {
		// Assignment --> Identifier = Expression ;
		Variable target = new Variable(functionName);
		Expression result = r;
		Return R = new Return(target, result);
		match(TokenType.Semicolon);
		return R;
	}

	private CallStatement callStatement(String functionName) {
		// Assignment --> Identifier = Expression ;
		match(TokenType.LeftParen);
		String name = functionName;
		CallStatement c;
		if (token.type() != TokenType.RightParen) {
			Expressions args = new Expressions();
			Expression e = expression();
			args.add(e);
			while (token.type() == TokenType.Comma) {
				token = lexer.next();
				e = expression();
				args.add(e);
			}
			c = new CallStatement(name, args);
		} else
			c = new CallStatement(name);
		match(TokenType.RightParen);
		match(TokenType.Semicolon);
		return c;
	}

	private Expression expression() {
		// Expression --> Conjunction { || Conjunction }
		Expression ex1 = conjunction();
		while (token.type().equals(TokenType.Or)) {
			Operator op = new Operator(token.value());
			match(TokenType.Or);
			Expression ex2 = conjunction();
			ex1 = new Binary(op, ex1, ex2);
		}
		return ex1; // student exercise
	}

	private Expression conjunction() {
		// Conjunction --> Equality { && Equality }
		Expression ex1 = equality();
		while (token.type().equals(TokenType.And)) {
			Operator op = new Operator(token.value());
			match(TokenType.Or);
			Expression ex2 = equality();
			ex1 = new Binary(op, ex1, ex2);
		}
		return ex1; // student exercise
	}

	private Expression equality() {
		// Equality --> Relation [ EquOp Relation ]
		Expression ex1 = relation();

		while (isEqualityOp()) {
			Operator op = new Operator(token.value());
			match(token.type());
			Expression ex2 = relation();
			ex1 = new Binary(op, ex1, ex2);
		}

		return ex1; // student exercise
	}

	private Expression relation() {
		// Relation --> Addition [RelOp Addition]
		Expression ex1 = addition();

		while (isRelationalOp()) {
			Operator op = new Operator(token.value());
			match(token.type());
			Expression ex2 = addition();
			ex1 = new Binary(op, ex1, ex2);
		}
		return ex1; // student exercise
	}

	private Expression addition() {
		// Addition --> Term { AddOp Term }
		Expression e = term();
		while (isAddOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term2 = term();
			e = new Binary(op, e, term2);
			Binary test = new Binary(op, e, term2);
		}
		return e;
	}

	private Expression term() {
		// Term --> Factor { MultiplyOp Factor }
		Expression e = factor();
		while (isMultiplyOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term2 = factor();
			e = new Binary(op, e, term2);
		}
		return e;
	}

	private Expression factor() {
		// Factor --> [ UnaryOp ] Primary || Call
		if (isUnaryOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term = primary();
			return new Unary(op, term);
		} else
			return primary();
	}

	private Expression primary() {
		// Primary --> Identifier | Literal | ( Expression )
		// | Type ( Expression )
		Expression e = null;
		if (token.type().equals(TokenType.Identifier)) {
			String tmp = match(TokenType.Identifier);
			if (token.type() == TokenType.LeftParen) {
				e = callExpression(tmp);
			} else {
				e = new Variable(tmp);
			}
		} else if (isLiteral()) {
			e = literal();
		} else if (token.type().equals(TokenType.LeftParen)) {
			token = lexer.next();
			e = expression();
			match(TokenType.RightParen);
		} else if (isType()) {
			Operator op = new Operator(match(token.type()));
			match(TokenType.LeftParen);
			Expression term = expression();
			match(TokenType.RightParen);
			e = new Unary(op, term);
		} else if (token.type().equals(TokenType.ReadInt)) { // 수정
			token = lexer.next();
			match(TokenType.LeftParen);
			match(TokenType.RightParen);
			e = new ReadInt();
		} else if (token.type().equals(TokenType.ReadFloat)) {
			token = lexer.next();
			match(TokenType.LeftParen);
			match(TokenType.RightParen);
			e = new ReadFloat();
		} else
			error("Identifier | Literal | ( | Type");
		return e;
	}

	private Expression callExpression(String functionName) {
		// Assignment --> Identifier = Expression ;
		token = lexer.next();
		String name = functionName;
		Expression c;
		if (token.type() != TokenType.RightParen) {
			Expressions args = new Expressions();
			Expression e = expression();
			args.add(e);
			while (token.type() == TokenType.Comma) {
				token = lexer.next();
				e = expression();
				args.add(e);
			}
			c = new CallExpression(name, args);
		} else
			c = new CallExpression(name);
		match(TokenType.RightParen);
		return c;
	}

	private Value literal() {
		Value v = null;
		Type t = null;
		switch (token.type()) {
		case IntLiteral:
			v = new IntValue(Integer.parseInt(token.value()));
			break;
		case FloatLiteral:
			v = new FloatValue(Float.parseFloat(token.value()));
			break;
		case CharLiteral:
			v = new CharValue((token.value()).charAt(0));
			break;
		case True:
		case False:
			v = new BoolValue(Boolean.parseBoolean(token.value()));
			break;
		}
		token = lexer.next();
		return v; // student exercise
	}

	private boolean isAddOp() {
		return token.type().equals(TokenType.Plus) || token.type().equals(TokenType.Minus);
	}

	private boolean isMultiplyOp() {
		return token.type().equals(TokenType.Multiply) || token.type().equals(TokenType.Divide);
	}

	private boolean isUnaryOp() {
		return token.type().equals(TokenType.Not) || token.type().equals(TokenType.Minus);
	}

	private boolean isEqualityOp() {
		return token.type().equals(TokenType.Equals) || token.type().equals(TokenType.NotEqual);
	}

	private boolean isRelationalOp() {
		return token.type().equals(TokenType.Less) || token.type().equals(TokenType.LessEqual)
				|| token.type().equals(TokenType.Greater) || token.type().equals(TokenType.GreaterEqual);
	}

	private boolean isType() {
		return token.type().equals(TokenType.Int) || token.type().equals(TokenType.Bool)
				|| token.type().equals(TokenType.Float) || token.type().equals(TokenType.Char);
	}

	private boolean isLiteral() {
		return token.type().equals(TokenType.IntLiteral) || isBooleanLiteral()
				|| token.type().equals(TokenType.FloatLiteral) || token.type().equals(TokenType.CharLiteral);
	}

	private boolean isBooleanLiteral() {
		return token.type().equals(TokenType.True) || token.type().equals(TokenType.False);
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer("test.txt"));
		Program prog = parser.program();
		prog.display(); // display abstract syntax tree
	} // main

} // Parser
