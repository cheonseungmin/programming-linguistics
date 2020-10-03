package programing;

import java.util.*;

public class State extends HashMap<Variable, Value> {
	// Defines the set of variables and their associated values
	// that are active during interpretation

	Environment gamma; // <이름, 주소>
	Memory mu; // <주소, 값>
	int a;
	int dlink;

	public State() {
		gamma = new Environment();
		mu = new Memory(Value.mkValue(Type.UNUSED), 1024);
		a = 0;
		dlink = 0;
	}

	public State(State sigma) {
		gamma = new Environment();
		mu = new Memory(Value.mkValue(Type.UNUSED), 1024);
		a = 0;
		dlink = 0;
		for (int i = 0; i < sigma.gamma.size(); i++)
			this.gamma.add(sigma.gamma.get(i));
		for (int i = 0; i < sigma.mu.size(); i++)
			this.mu.set(i, sigma.mu.get(i));

		this.a = sigma.a;
		this.dlink = sigma.dlink;

	}

	public State(Variable key, Value val) {
		gamma = new Environment();
		gamma.add(new Pair(key.toString(), 0));
		mu = new Memory(Value.mkValue(Type.UNUSED), 1024);
		mu.set(0, val);
		a = 1;
		dlink = 1;
	}

	public State onion(Variable key, Value val) {
		int i = 0;
		for (i = 0; i < gamma.size(); i++)
			if (key.toString().equals(gamma.get(i).name))
				break;

		mu.set(gamma.get(i).address, val);
		gamma.set(i, new Pair(key.toString(), gamma.get(i).address));
		return this;
	}

	public State onion(State t) { // 자기를 t의 값으로 바꿈
		for (int i = 0; i < t.gamma.size(); i++) { // t의 값 i
			for (int j = 0; j < gamma.size(); j++) { // 자신의 값 j
				if (t.gamma.get(i).name.equals(gamma.get(j).name)) {
					mu.set(gamma.get(j).address, t.mu.get(t.gamma.get(i).address));
				}
			}
		}
		return this;
	}

	public void display() {
		for (int i = 0; i < gamma.size(); i++) {
			System.out.println(gamma.get(i).name + " : address " + gamma.get(i).address + " " + "value " + mu.get(gamma.get(i).address));
		}
	}

	public State allocate(Declarations ds) {
		for (int i = 0; i < ds.size(); i++) {
			Pair p = new Pair(ds.get(i).v.toString(), a);
			gamma.add(p);
			mu.set(a, null);
			a++;
		}
		return this;
	}

	public State deallocate(Declarations ds) {
		for(int i=gamma.size()-1; i>= dlink; i--) {
			mu.set(gamma.get(i).address, null);
			gamma.remove(i);
			a--;
		}
		
		return this;
	}

	public State minus(int a1, int d1) {
		for (int i = a1-1; d1 <= i; i--) gamma.remove(gamma.size()-1);
		

		return this;
	}
	
}
