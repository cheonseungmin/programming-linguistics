package programing;

import java.util.Scanner;

//Following is the semantics class:
//The meaning M of a Statement is a State
//The meaning M of a Expression is a Value

public class Test {

	State M(Program p) {
		
		return M(p.body, initialState(p.decpart));
	}

	State initialState(Declarations d) {
		State state = new State();
		Value intUndef = new IntValue();
		for (Declaration decl : d)
			state.put(decl.v, Value.mkValue(decl.t));
		return state;
	}

	State M(Statement s, State state) {
		if (s instanceof Skip)
			return M((Skip) s, state);
		if (s instanceof Assignment)
			return M((Assignment) s, state);
		if (s instanceof Conditional)
			return M((Conditional) s, state);
		if (s instanceof Loop)
			return M((Loop) s, state);
		if (s instanceof Block)
			return M((Block) s, state);
		if (s instanceof Put)
            return M((Put) s, state);
		throw new IllegalArgumentException("should never reach here");
	}

	State M(Skip s, State state) {
		return state;
	}

	State M(Assignment a, State state) {
		return state.onion(a.target, M(a.source, state));
	}

	State M(Block b, State state) {
		for (Statement s : b.members)
			state = M(s, state);
		return state;
	}

	State M(Conditional c, State state) {
		if (M(c.test, state).boolValue())
			return M(c.thenbranch, state);
		else
			return M(c.elsebranch, state);
	}

	State M(Loop l, State state) {
		if (M(l.test, state).boolValue())
			return M(l, M(l.body, state));
		else
			return state;
	}
	
	State M(Put p, State state) {
		Value v = state.get(p.expression);
		System.out.println("Meaning of put : " + v);
		return state;
	}

	Value applyBinary(Operator op, Value v1, Value v2) {
		StaticTypeCheck.check(!v1.isUndef() && !v2.isUndef(), "reference to undef value");
		if (op.val.equals(Operator.INT_PLUS))
			return new IntValue(v1.intValue() + v2.intValue());
		if (op.val.equals(Operator.INT_MINUS))
			return new IntValue(v1.intValue() - v2.intValue());
		if (op.val.equals(Operator.INT_TIMES))
			return new IntValue(v1.intValue() * v2.intValue());
		if (op.val.equals(Operator.INT_DIV))
			return new IntValue(v1.intValue() / v2.intValue());

		// student exercise

		// int
		if (op.val.equals(Operator.INT_LT)) {
			if (v1.intValue() < v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.INT_LE)) {
			if (v1.intValue() <= v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.INT_EQ)) {
			if (v1.intValue() == v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.INT_NE)) {
			if (v1.intValue() != v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.INT_GT)) {
			if (v1.intValue() > v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.INT_GE)) {
			if (v1.intValue() >= v2.intValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}

		// float
		if (op.val.equals(Operator.FLOAT_PLUS))
			return new FloatValue(v1.floatValue() + v2.floatValue());
		if (op.val.equals(Operator.FLOAT_MINUS))
			return new FloatValue(v1.floatValue() - v2.floatValue());
		if (op.val.equals(Operator.FLOAT_TIMES))
			return new FloatValue(v1.floatValue() * v2.floatValue());
		if (op.val.equals(Operator.FLOAT_DIV))
			return new FloatValue(v1.floatValue() / v2.floatValue());

		if (op.val.equals(Operator.FLOAT_LT)) {
			if (v1.floatValue() < v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.FLOAT_LE)) {
			if (v1.floatValue() <= v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.FLOAT_EQ)) {
			if (v1.floatValue() == v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.FLOAT_NE)) {
			if (v1.floatValue() != v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.FLOAT_GT)) {
			if (v1.floatValue() > v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.FLOAT_GE)) {
			if (v1.floatValue() >= v2.floatValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}

		// char
		if (op.val.equals(Operator.CHAR_LT)) {
			if (v1.charValue() < v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.CHAR_LE)) {
			if (v1.charValue() <= v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.CHAR_EQ)) {
			if (v1.charValue() == v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.CHAR_NE)) {
			if (v1.charValue() != v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.CHAR_GT)) {
			if (v1.charValue() > v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.CHAR_GE)) {
			if (v1.charValue() >= v2.charValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}

		// boolean
		if (op.val.equals(Operator.AND)) {
			if (true == v1.boolValue() == v2.boolValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.OR)) {
			if ((true == v1.boolValue()) || (true == v2.boolValue())) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}

		if (op.val.equals(Operator.BOOL_EQ)) {
			if (v1.boolValue() == v2.boolValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}
		if (op.val.equals(Operator.BOOL_NE)) {
			if (v1.boolValue() != v2.boolValue()) {
				return new BoolValue(true);
			} else
				return new BoolValue(false);
		}

		throw new IllegalArgumentException("should never reach here");
	}

	Value applyUnary(Operator op, Value v) {
		StaticTypeCheck.check(!v.isUndef(), "reference to undef value");
		if (op.val.equals(Operator.NOT))
			return new BoolValue(!v.boolValue());
		else if (op.val.equals(Operator.INT_NEG))
			return new IntValue(-v.intValue());
		else if (op.val.equals(Operator.FLOAT_NEG))
			return new FloatValue(-v.floatValue());
		else if (op.val.equals(Operator.I2F))
			return new FloatValue((float) (v.intValue()));
		else if (op.val.equals(Operator.F2I))
			return new IntValue((int) (v.floatValue()));
		else if (op.val.equals(Operator.C2I))
			return new IntValue((int) (v.charValue()));
		else if (op.val.equals(Operator.I2C))
			return new CharValue((char) (v.intValue()));
		throw new IllegalArgumentException("should never reach here");
	}

	Value M(Expression e, State state) {
		if (e instanceof Value)
			return (Value) e;
		if (e instanceof Variable)
			return (Value) (state.get(e));
		if (e instanceof Binary) {
			Binary b = (Binary) e;
			return applyBinary(b.op, M(b.term1, state), M(b.term2, state));
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			return applyUnary(u.op, M(u.term, state));
		}
        if (e instanceof ReadInt) {
        	ReadInt ri = (ReadInt)e;
        	System.out.print("input int value: ");
    		Scanner scan = new Scanner(System.in);
    		ri.v = new IntValue(scan.nextInt());
        	return ri.v;
		}
        if (e instanceof ReadFloat) {
        	ReadFloat rf = (ReadFloat)e;
        	System.out.print("input float value: ");
    		Scanner scan = new Scanner(System.in);
    		rf.v = new FloatValue(scan.nextFloat());
        	return rf.v;
		}
		throw new IllegalArgumentException("should never reach here");
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer("test.txt"));
		Program prog = parser.program();
		// prog.display(); // student exercise
//		System.out.println("\nBegin type checking...");
//		System.out.println("Type map:");
		TypeMap map = StaticTypeCheck.typing(prog.decpart);
//		map.display(); // student exercise
		StaticTypeCheck.V(prog);
		Program out = TypeTransformer.T(prog, map);
//		System.out.println("Output AST");
//		out.display(); // student exercise
		Semantics semantics = new Semantics();
		State state = semantics.M(out);
		System.out.println();
		System.out.println("Final State");
		state.display(); // student exercise
	}
}
