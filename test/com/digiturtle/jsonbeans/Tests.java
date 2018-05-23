package com.digiturtle.jsonbeans;

public class Tests {
	
	@FunctionalInterface
	public interface TestCase {
		void runTest();
	}
	
	public static long runAndAverage(TestCase f, int times) {
		long totalNs = 0;
		for (int i = 0; i < times; i++) {
			long ns0 = System.nanoTime();
			f.runTest();
			totalNs += (System.nanoTime() - ns0);
		}
		return totalNs / times;
	}
	
	public static void assertEqual(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return;
		}
		if (!o1.equals(o2)) {
			throw new IllegalStateException("o1 != o2");
		}
	}

}
