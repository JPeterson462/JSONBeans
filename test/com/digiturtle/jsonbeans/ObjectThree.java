package com.digiturtle.jsonbeans;

import java.util.Arrays;

public class ObjectThree {

	public int[] arr2;
	
	public ObjectThreeSubOne[] arr;
	
	public String toString() {
		return "{" + Arrays.toString(arr) + ", " + Arrays.toString(arr2) + "}";
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
