package com.digiturtle.jsonbeans;

import java.io.IOException;

public class Test1 {
	
	public static void main(String[] args) throws IOException {
		Tests.runAndAverage(Test1::runTest, 10);
		long avgNs = Tests.runAndAverage(Test1::runTest, 100);
		System.out.println(avgNs + "ns, " + avgNs/1_000 + "us, " + avgNs/1_000_000 + "ms");
	}
	
	public static void runTest() {
		try {
			String letters = Utils.readFile("test1.json");
			ObjectOne objectOne = (ObjectOne) JSONBeans.readBean(letters.toCharArray(), ObjectOne.class, null);
			ObjectOne testObject = new ObjectOne();
			testObject.realNumber = 0.14159265358f;
			testObject.integer = 3;
			testObject.string = "Hello World";
			testObject.subOne = new ObjectOne.ObjectOneSubOne();
			testObject.subOne.realNumber = 0.71278;
			testObject.subOne.longInteger = 2;
			Tests.assertEqual(objectOne, testObject);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	
	}

}
