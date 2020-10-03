package programing;

import java.util.Scanner;

//Following is the semantics class:
//The meaning M of a Statement is a State
//The meaning M of a Expression is a Value

public class Semantics {
	State sigmag;
	Functions fs;

	State M(Program p) {
		sigmag = new State();
		sigmag = sigmag.allocate(p.globals);
		sigmag.dlink = sigmag.a;
		fs = p.functions;
		
		return M(p.functions, sigmag);	
	}

	
	State M(Functions fs, State state) {
		Function main = findFunction("main");
		State sigma = new State(sigmag);
		sigma.dlink = sigmag.a;
		sigma = sigma.allocate(main.locals);
		System.out.println("main start");
		System.out.println();
		sigma.display();
		System.out.println();
		sigma = M(main.body, sigma);
		sigma = sigma.deallocate(main.locals);
		sigmag = sigmag.onion(sigma);
		System.out.println("main end");
		
		return sigmag;
	}
	
	Function findFunction(String name) {
		int i;
		for(i=0; i<fs.size(); i++) if(fs.get(i).id == name) break;
		if(i == fs.size()) {
			System.out.println("main이 존재하지 않습니다");
			System.exit(1);
		}
		
		return fs.get(i);
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
		if (s instanceof CallStatement)
			return M((CallStatement) s, state);
		
		throw new IllegalArgumentException("should never reach here");
	}

	State M(Skip s, State state) {
		return state;
	}

	State M(Assignment a, State state) {
		sigmag = state.onion(a.target, M(a.source, state));
		return sigmag;
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
	
	State M(Block b, State sigma) {
		int n = b.members.size();
		Statement s;
		for(int i=0; i<n; i++) {
			s = (Statement)b.members.get(i);
			sigma = M(s, sigma);
			if(s instanceof Return) return sigma;
		}
		return sigma;
	}
	
	State M(Put p, State state) {
		Value v = state.get(p.expression);
		System.out.println("Meaning of put : " + v);
		return state;
	}
	
	State M(CallStatement c, State state) {
		int i = 0;
		for(i=0; i<fs.size(); i++) if(fs.get(i).id.equals(c.name)) break;
		addFrame(state, c, fs.get(i));

		return state;
	}
	
	State M(Return r,  State sigma) {
		return sigma.onion(new State(r.target, M(r.result, sigma)));
	}
	
	State addFrame(State current, CallStatement c, Function f) {
		System.out.println(c.name + " start");
		System.out.println();
		State s = new State(current);
		s = s.minus(current.a, current.dlink);
		s = s.onion(sigmag);
		s = s.allocate(f.params);
	
		for(int i=0; i<f.params.size(); i++) { // 인수 전달
			Expression e = (Expression)c.args.get(i);
			Declaration d = (Declaration)f.params.get(i);
			Variable v = (Variable)d.v;
			
			State test = new State(v, M(e, current));
			s.onion(new State(v, M(e, current)));
		}
		s = s.allocate(f.locals); // 지역 변수 할당
		Declarations ds = new Declarations();
		ds.add(new Declaration(new Variable(f.id), f.type));
		s = s.allocate(ds);
		
		s.dlink = current.a;
		s = M(f.body, new State(s));
		
		System.out.println(c.name + " end & sigma");
		s.display();
		System.out.println();
		return s;
	}

	///////////////////////////////////////////////
	
	Value applyBinary (Operator op, Value v1, Value v2) {
        StaticTypeCheck.check( v1.type( ) == v2.type( ),
                               "mismatched types");
        if (op.ArithmeticOp( )) {
            if (v1.type( ) == Type.INT) {
                if (op.val.equals(Operator.PLUS)) 
                    return new IntValue(v1.intValue( ) + v2.intValue( ));
                if (op.val.equals(Operator.MINUS)) 
                    return new IntValue(v1.intValue( ) - v2.intValue( ));
                if (op.val.equals(Operator.TIMES)) 
                    return new IntValue(v1.intValue( ) * v2.intValue( ));
                if (op.val.equals(Operator.DIV)) 
                    return new IntValue(v1.intValue( ) / v2.intValue( ));
            }
            // student exercise
        }
        // student exercise
        throw new IllegalArgumentException("should never reach here");
    } 
    
    Value applyUnary (Operator op, Value v) {
        if (op.val.equals(Operator.NOT))
            return new BoolValue(!v.boolValue( ));
        else if (op.val.equals(Operator.NEG))
            return new IntValue(-v.intValue( ));
        else if (op.val.equals(Operator.NEG))
            return new FloatValue(-v.floatValue( ));
        else if (op.val.equals(Operator.FLOAT))
            return new FloatValue((float)(v.intValue( ))); 
        else if (op.val.equals(Operator.INT))
            return new IntValue((int)(v.floatValue( )));
        else if (op.val.equals(Operator.INT))
            return new IntValue((int)(v.charValue( )));
        else if (op.val.equals(Operator.CHAR))
            return new CharValue((char)(v.intValue( )));
        throw new IllegalArgumentException("should never reach here");
    } 

    Value M (Expression e, State sigma) {
        if (e instanceof Value) 
            return (Value)e;
        if (e instanceof Variable) if (e instanceof Variable) {
			for(int i=sigma.gamma.size()-1; i>-1; i--) {
				if(sigma.gamma.get(i).name.equals(e.toString())) {
					return sigma.mu.get(sigma.gamma.get(i).address);
				}
			}
			return (Value) (sigma.get(e));
		}
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            return applyBinary (b.op, 
                       M(b.term1, sigma), M(b.term2, sigma));
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            return applyUnary(u.op, M(u.term, sigma));
        }
        throw new IllegalArgumentException("should never reach here");
    }

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer("test.txt"));
		Program prog = parser.program();
		// prog.display(); // student exercise
//		System.out.println("\nBegin type checking...");
//		System.out.println("Type map:");
//		TypeMap map = StaticTypeCheck.typing(prog.decpart);
//		map.display(); // student exercise
		StaticTypeCheck.V(prog);
//		Program out = TypeTransformer.T(prog, map);
//		System.out.println("Output AST");
//		out.display(); // student exercise
		Semantics semantics = new Semantics();
		State state = semantics.M(prog);
	}
}
