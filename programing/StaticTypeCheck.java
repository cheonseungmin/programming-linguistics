package programing;

//StaticTypeCheck.java

import java.util.*;

//Static type checking for Clite is defined by the functions 
//V and the auxiliary functions typing and typeOf.  These
//functions use the classes in the Abstract Syntax of Clite.

public class StaticTypeCheck {

	public static TypeMap typing(Declarations d) {
		TypeMap map = new TypeMap();
		for (Declaration di : d)
			map.put(di.v, di.t);
		return map;
	}

	public static TypeMap typing(Declarations d1, Functions d2) {
		TypeMap map = new TypeMap();
		for (Declaration di : d1)
			map.put(di.v, di.t);
		for (Function f : d2)
			map.put(new Variable(f.id), f.type);
		return map;
	}

	public static void check(boolean test, String msg) {
		if (test)
			return;
		System.err.println(msg);
		System.exit(1);
	}

	public static void V(Declarations d) {
		for (int i = 0; i < d.size() - 1; i++)
			for (int j = i + 1; j < d.size(); j++) {
				Declaration di = d.get(i);
				Declaration dj = d.get(j);
				check(!(di.v.equals(dj.v)), "duplicate declaration: " + dj.v);
			}
	}

	public static void V(Program p) {
		V(p.globals, p.functions); // 이름 중복 검사
		boolean foundmain = false;
		TypeMap tmg = typing(p.globals, p.functions);
		for (Function f : p.functions) {
			if (f.id.equals("main")) {
				if (foundmain)
					check(false, "Duplicate main function"); // 메인 중복 검사
				else
					foundmain = true;
			}

			V(f.params, f.locals); // 이름 중복 검사
			TypeMap tmf = typing(f.params).onion(typing(f.locals));
			tmf = tmg.onion(tmf);
			V(f.body, tmf, p.functions);
		}

		return;
	}

	public static void V(Declarations params, Declarations locals) {
		Declarations temp = new Declarations();

		for (int i = 0; i < params.size(); i++)
			temp.add(params.get(i));
		for (int i = 0; i < locals.size(); i++)
			temp.add(locals.get(i));

		V(temp);
	}

	public static void V(Declarations globals, Functions functions) {
		Declarations temp = new Declarations();

		Function f;
		for (int i = 0; i < functions.size(); i++) {
			f = functions.get(i);
			Declaration callExpression = new Declaration(new Variable(f.id), f.type);
			temp.add(callExpression);
		}

		for (int i = 0; i < globals.size(); i++)
			temp.add(globals.get(i)); // temp에 globals 저장

		V(temp); // 함수 이름과 전역 변수들 중복 검사

		for (int i = 0; i < globals.size(); i++)
			temp.remove(globals.get(i)); // 지역변수 검사 위해 삭제

		for (int i = 0; i < functions.size(); i++) {
			f = functions.get(i);
			for (int j = 0; j < f.params.size(); j++)
				temp.add(f.params.get(j));
			for (int j = 0; j < f.locals.size(); j++)
				temp.add(f.locals.get(j));
			V(temp); // 함수 이름과 매개 변수, 지역변수들 중복 검사
			for (int j = 0; j < f.params.size(); j++)
				temp.remove(f.params.get(j));
			for (int j = 0; j < f.locals.size(); j++)
				temp.remove(f.locals.get(j));
		}
	}

	public static void V(CallStatement c, Function f, TypeMap tm) {
		check((c.args.size() == f.params.size()), "매개변수 수가 다름: " + c.name); // 매개변수 수가 다름
		for(int i=0; i<f.params.size(); i++) {
			Type t = typeOf(c.args.get(i), tm);
			check((t == f.params.get(i).t), "매개변수 타입이 다름: " + f.params.get(i).v.toString());
		}
	}

	public static Type typeOf(Expression e, TypeMap tm) {
		if (e instanceof Value)
			return ((Value) e).type;
		if (e instanceof Variable) {
			Variable v = (Variable) e;
			check(tm.containsKey(v), "undefined variable: " + v);
			return (Type) tm.get(v);
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			if (b.op.ArithmeticOp())
				if (typeOf(b.term1, tm) == Type.FLOAT)
					return (Type.FLOAT);
				else
					return (Type.INT);
			if (b.op.RelationalOp() || b.op.BooleanOp())
				return (Type.BOOL);
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			if (u.op.NotOp())
				return (Type.BOOL);
			else if (u.op.NegateOp())
				return typeOf(u.term, tm);
			else if (u.op.intOp())
				return (Type.INT);
			else if (u.op.floatOp())
				return (Type.FLOAT);
			else if (u.op.charOp())
				return (Type.CHAR);
		}
		if (e instanceof ReadInt) {
			return (Type.INT);
		}
		if (e instanceof ReadFloat) {
			return (Type.FLOAT);
		}
		if (e instanceof CallExpression) {
			CallExpression c = (CallExpression) e;
			Variable v = new Variable(c.name);
			return (Type) tm.get(v);
		}
		throw new IllegalArgumentException("should never reach here");
	}

	public static void V(Expression e, TypeMap tm) {
		if (e instanceof Value)
			return;
		if (e instanceof Variable) {
			Variable v = (Variable) e;
			check(tm.containsKey(v), "undeclared variable: " + v);
			return;
		}
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			Type typ1 = typeOf(b.term1, tm);
			Type typ2 = typeOf(b.term2, tm);
			V(b.term1, tm);
			V(b.term2, tm);
			if (b.op.ArithmeticOp()) {
				check(typ1 == typ2 && (typ1 == Type.INT || typ1 == Type.FLOAT), "type error for " + b.op);
			} else if (b.op.RelationalOp()) {
				check(typ1 == typ2, "type error for " + b.op);
			} else if (b.op.BooleanOp())
				check(typ1 == Type.BOOL && typ2 == Type.BOOL, b.op + ": non-bool operand");
			else
				throw new IllegalArgumentException("should never reach here");
			return;
		}
		// student exercise
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			V(u.term, tm);
			return;
		}
		if (e instanceof ReadInt) {
			return;
		}
		if (e instanceof ReadFloat) {
			return;
		}
		if (e instanceof CallExpression) {
			return;
		}
		throw new IllegalArgumentException("should never reach here");
	}

	public static void V(Statement s, TypeMap tm, Functions fs) {
		if (s == null)
			throw new IllegalArgumentException("AST error: null statement");
		if (s instanceof Skip)
			return;
		if (s instanceof Assignment) {
			Assignment a = (Assignment) s;
			check(tm.containsKey(a.target), " undefined target in assignment: " + a.target);
			V(a.source, tm);
			Type ttype = (Type) tm.get(a.target);
			Type srctype = typeOf(a.source, tm);
			if (ttype != srctype) {
				if (ttype == Type.FLOAT)
					check(srctype == Type.INT, "mixed mode assignment to " + a.target);
				else if (ttype == Type.INT)
					check(srctype == Type.CHAR, "mixed mode assignment to " + a.target);
				else
					check(false, "mixed mode assignment to " + a.target);
			}
			return;
		}
		// student exercise
		if (s instanceof Block) {
			Block b = (Block) s;
			for (int i = 0; i < b.members.size(); i++) {
				V(b.members.get(i), tm, fs);
			}
			return;
		}
		if (s instanceof Conditional) {
			Conditional c = (Conditional) s;
			Expression ex = c.test;
			V(ex, tm);

			Statement t = c.thenbranch;
			V(t, tm, fs);

			Statement e = c.elsebranch;
			V(e, tm, fs);
			return;
		}
		if (s instanceof Loop) {
			Loop l = (Loop) s;
			Expression t = l.test;
			V(t, tm);
			Statement b = l.body;
			V(b, tm, fs);
			return;
		}
		if (s instanceof Put) {
			Put p = (Put) s;
			Expression e = p.expression;
			V(e, tm);
			return;
		}
		if (s instanceof Return) {
			Variable fid = ((Return) s).target;
			check(tm.containsKey(fid), "undefined function: " + fid); // 함수가 있는지 체크
			V(((Return) s).result, tm); // ex) return a 일때 a가 잇는지 체크
			check(((Type) tm.get(fid)).equals(typeOf(((Return) s).result, tm)), "incorrect return type");
			// 리턴 타입과 함수 타입이 같은지 체크
			return;
		}
		if (s instanceof CallStatement) {
			CallStatement c = (CallStatement) s;
			int i;
			for(i=0; i<fs.size(); i++) if(c.name.equals(fs.get(i).id)) break;
			check(!(i == fs.size()), "존재하지 않는 함수를 호출했습니다."); 
			V(c, fs.get(i), tm); // 매개변수 타입 검사
			return;
		}
		throw new IllegalArgumentException("should never reach here");
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer("test.txt"));
//		Parser parser = new Parser(new Lexer("fibonacci.txt"));
		Program prog = parser.program(); // p.ds, p.b를 가진 객체 prog
		prog.display(); // student exercise
		System.out.println("Type map:\n");
//		TypeMap map = typing(prog.decpart);
//		map.display(); // student exercise
		V(prog);
		System.out.println("type check success!");
	} // main

} // class StaticTypeCheck
