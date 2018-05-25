package com.digiturtle.jsonbeans;

import java.util.ArrayList;

public class Test3 {
	
	public static void main(String[] args) {
		Tests.runAndAverage(Test3::runTest, 10);
		long avgNs = Tests.runAndAverage(Test3::runTest, 100);
		System.out.println(avgNs + "ns, " + avgNs/1_000 + "us, " + avgNs/1_000_000 + "ms");
	}
	
	public static void runTest() {
		ArrayList<String> map = new ArrayList<>();
		map.add("apple");
		map.add("banana");
		Tests.assertEqual(map, JSONBeans.readBean(JSONBeans.writeBean(map), ArrayList.class, null));
	}
	
	public static ObjectTwo newObject(int[] arr0, float[] arr1) {
		ObjectTwo object = new ObjectTwo();
		object.arr = arr0;
		object.size = arr0.length;
		object.arr2 = arr1;
		return object;
	}

}
