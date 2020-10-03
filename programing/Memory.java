package programing;
import java.util.*;
public class Memory extends ArrayList<Value> {
	int size;
	Memory(Value v, int s) {
		size = s;
		for(int i=0; i<size; i++) {
			this.add(v);
		}
	}
	
	public void display() {
		for(Value v: this) {
			System.out.println(v);
		}
	}
}
