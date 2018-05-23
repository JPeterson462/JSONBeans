package com.digiturtle.jsonbeans;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {

	static String readFile(String path) throws IOException {
		return readFile(path, Charset.defaultCharset());
	}
	
	static String readFile(String path, Charset encoding) 
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}