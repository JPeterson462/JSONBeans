package com.digiturtle.jsonbeans;

import java.util.Arrays;

public class ObjectThree {
	
	public ObjectThreeSubOne[] arr;
	
	public String toString() {
		return "{" + Arrays.toString(arr) + "}";
	}
	
	public static class ObjectThreeSubOne {
		
		int n;
		
		public ObjectThreeSubOne() {
			
		}
		
		public String toString() {
			return "ObjectThreeSubOne[" + n + "]";
		}
		
	}

}
