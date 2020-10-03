package programing;

import java.util.*;

public class TypeMap extends HashMap<Variable, Type> { 

// TypeMap is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.

	public void display() {
		Set<Variable> keys = this.keySet();
		Iterator<Variable> it = keys.iterator();
		while(it.hasNext()) {
			Variable v = it.next();
			Type t = this.get(v);
			System.out.println("type : " + t + ", name : " + v);
		}
		System.out.println("\n");
	}
	
	public TypeMap onion(TypeMap t) {
		TypeMap tm = new TypeMap();
		tm.putAll(this);
		tm.putAll(t);
		return tm;
	}
}

