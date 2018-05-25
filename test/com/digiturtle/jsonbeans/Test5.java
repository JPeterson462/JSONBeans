package com.digiturtle.jsonbeans;

public class Test5 {

	public static void main(String[] args) {
		Tests.runAndAverage(Test5::runTest, 10);
		long avgNs = Tests.runAndAverage(Test5::runTest, 100);
		System.out.println(avgNs + "ns, " + avgNs/1_000 + "us, " + avgNs/1_000_000 + "ms");
	}
	
	public static void runTest() {
		ObjectThree three = new ObjectThree();
		three.arr = new ObjectThree.ObjectThreeSubOne[3];
		for (int i = 0; i < 3; i++) {
			three.arr[i] = new ObjectThree.ObjectThreeSubOne();
			three.arr[i].n = i + 1;
		}
		System.out.println(three);
		System.out.println(JSONBeans.writeBean(three));
		System.out.println(JSONBeans.readBean(JSONBeans.writeBean(three), ObjectThree.class, null));
	}
	
}
