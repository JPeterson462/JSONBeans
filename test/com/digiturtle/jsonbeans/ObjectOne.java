package com.digiturtle.jsonbeans;

public class ObjectOne {
	
	public float realNumber;
	
	public int integer;
	
	public String string;
	
	public ObjectOneSubOne subOne;
	
	public ObjectOne() {
		
	}
	
	public String toString() {
		return "ObjectOne[" + realNumber + ", " + integer + ", " + string + ", " + subOne + "]";
	}
	
	public boolean equals(Object o) {
		if (o instanceof ObjectOne) {
			ObjectOne oo = (ObjectOne) o;
			return oo.realNumber == realNumber && oo.integer == integer && oo.string.equals(string) && ((oo.subOne == null && subOne == null) || oo.subOne.equals(subOne));
		}
		return false;
	}
	
	public static class ObjectOneSubOne {
		
		public double realNumber;
		
		public long longInteger;

		public ObjectOneSubOne() {
			
		}
		
		public String toString() {
			return "ObjectOneSubOne[" + realNumber + ", " + longInteger + "]";
		}
		
		public boolean equals(Object o) {
			if (o instanceof ObjectOneSubOne) {
				ObjectOneSubOne oo = (ObjectOneSubOne) o;
				return oo.realNumber == realNumber && oo.longInteger == longInteger;
			}
			return false;
		}
		
	}
	
}
