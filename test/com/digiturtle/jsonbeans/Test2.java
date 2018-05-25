package com.digiturtle.jsonbeans;

import java.io.IOException;

public class Test2 {

	public static void main(String[] args) throws IOException {
		Tests.runAndAverage(Test2::runTest, 10);
		long avgNs = Tests.runAndAverage(Test2::runTest, 100);
		System.out.println(avgNs + "ns, " + avgNs/1_000 + "us, " + avgNs/1_000_000 + "ms");
	}
	
	public static void runTest() {
		try {
			String letters = Utils.readFile("test2.json");
			ObjectTwo objectTwo = JSONBeans.readBean(letters.toCharArray(), ObjectTwo.class, null);
			ObjectTwo testObject = new ObjectTwo();
			testObject.arr = new int[] { 1, 2, 3 };
			testObject.size = 3;
			testObject.arr2 = new float[] { 1.5f };
			Tests.assertEqual(objectTwo, testObject);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
