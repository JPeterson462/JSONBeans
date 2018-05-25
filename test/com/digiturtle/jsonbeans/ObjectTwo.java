package com.digiturtle.jsonbeans;

import java.util.Arrays;

public class ObjectTwo {
	
	public int[] arr;
	
	public float[] arr2;
	
	public int size;
	
	public boolean b;
	
	public ObjectTwo() {
		
	}
	
	public String toString() {
		return Arrays.toString(arr);
	}
	
	public boolean equals(Object o) {
		return o instanceof ObjectTwo && Arrays.equals(((ObjectTwo) o).arr, arr) && Arrays.equals(((ObjectTwo) o).arr2, arr2) && ((ObjectTwo) o).b == b;
	}

}
