package com.digiturtle.jsonbeans;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;

import ftljson.ParseListener;
import ftljson.ParserC;

public class JSONBeans {
	
	private static class BeanReader implements ParseListener {
		
		public final Stack<Object> instantiationStack = new Stack<>();
		
		private String objectEntry;
		
		private final Stack<ArrayList<Object>> listStack = new Stack<>();

		@Override
		public void beginObject() {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				instantiationStack.push(field.getType().newInstance());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | InstantiationException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void endObject() {
			Object object = instantiationStack.pop();
			Object o = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.set(object, o);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void booleanLiteral(boolean value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				field.setBoolean(object, value);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void doubleLiteral(double value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				if (field.getType().getName() == "java.lang.Float") {
					object.getClass().getDeclaredField(objectEntry).set(object, Float.valueOf((float) value));
				}
				else if (field.getType().getName() == "java.lang.Double") {
					object.getClass().getDeclaredField(objectEntry).set(object, Double.valueOf(value));
				}
				else if (field.getGenericType().getTypeName() == "float") {
					object.getClass().getDeclaredField(objectEntry).setFloat(object, (float) value);
				}
				else if (field.getGenericType().getTypeName() == "double") {
					object.getClass().getDeclaredField(objectEntry).setDouble(object, value);
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void longLiteral(long value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				if (field.getType().getName() == "java.lang.Integer") {
					object.getClass().getDeclaredField(objectEntry).set(object, Integer.valueOf((int) value));
				}
				else if (field.getType().getName() == "java.lang.Long") {
					object.getClass().getDeclaredField(objectEntry).set(object, Long.valueOf(value));
				}
				else if (field.getGenericType().getTypeName() == "int") {
					object.getClass().getDeclaredField(objectEntry).setInt(object, (int) value);
				}
				else if (field.getGenericType().getTypeName() == "long") {
					object.getClass().getDeclaredField(objectEntry).setLong(object, value);
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void stringLiteral(String value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				field.set(object, value);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void nullLiteral() {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				field.set(object, null);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void beginObjectEntry(String value) {
			objectEntry = value;
		}

		@Override
		public void beginList() {
			listStack.push(new ArrayList<>());
		}

		@Override
		public void endList() {
			Object object = instantiationStack.peek();
			ArrayList<Object> values = listStack.pop();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry);
				field.setAccessible(true);
				field.set(object, values.toArray((Object[]) Array.newInstance(field.getType().getComponentType(), values.size())));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}
		
		public Object toBean() {
			return instantiationStack.peek();
		}
		
	}
	
	public static Object readBean(char[] letters, Class<?> type) {
		BeanReader reader = new BeanReader();
		try {
			reader.instantiationStack.push(type.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		ParserC.json(letters, reader);
		return reader.toBean();
	}
	
	public static char[] writeBean(Object bean) {
		return null;//FIXME
	}

}
