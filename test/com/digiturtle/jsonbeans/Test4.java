package com.digiturtle.jsonbeans;

import java.util.HashMap;

public class Test4 {

	public static void main(String[] args) {
		Tests.runAndAverage(Test4::runTest, 10);
		long avgNs = Tests.runAndAverage(Test4::runTest, 100);
		System.out.println(avgNs + "ns, " + avgNs/1_000 + "us, " + avgNs/1_000_000 + "ms");
	}
	
	public static void runTest() {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("apple", 1);
		map.put("banana", 2);
//		System.out.println(new String(JSONBeans.writeBean(new WritableHashMap<String, Integer>(map))));
//		Tests.assertEqual(map, JSONBeans.readBean(JSONBeans.writeBean(map), WritableHashMap.class, null).toHashMap());
	}
	
}
