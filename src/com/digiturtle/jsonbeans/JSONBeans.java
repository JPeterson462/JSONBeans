package com.digiturtle.jsonbeans;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Stack;

import ftljson.ParseListener;
import ftljson.ParserC;

public class JSONBeans {
	
	@FunctionalInterface
	public interface ClassConstructor {
		
		public Object newInstance(Class<?> type);
		
	}
	
	public static void toArray(ArrayList<Object> list, Class<?> componentType, Object array) {
		if (componentType.equals(byte.class)) {
			for (int i = 0; i < list.size(); i++) {
				((byte[]) array)[i] = ((Byte) list.get(i)).byteValue();
			}
		}
		else if (componentType.equals(short.class)) {
			for (int i = 0; i < list.size(); i++) {
				((short[]) array)[i] = ((Short) list.get(i)).shortValue();
			}
		}
		else if (componentType.equals(int.class)) {
			for (int i = 0; i < list.size(); i++) {
				((int[]) array)[i] = ((Integer) list.get(i)).intValue();
			}
		}
		else if (componentType.equals(float.class)) {
			for (int i = 0; i < list.size(); i++) {
				((float[]) array)[i] = ((Float) list.get(i)).floatValue();
			}
		}
		else if (componentType.equals(long.class)) {
			for (int i = 0; i < list.size(); i++) {
				((long[]) array)[i] = ((Long) list.get(i)).longValue();
			}
		}
		else if (componentType.equals(double.class)) {
			for (int i = 0; i < list.size(); i++) {
				((double[]) array)[i] = ((Double) list.get(i)).doubleValue();
			}
		}
		else {
			list.toArray((Object[]) array);
		}
	}
	
	private static class BeanReader implements ParseListener {
		
		public final Stack<Object> instantiationStack = new Stack<>();
		
		private Stack<String> objectEntry = new Stack<>();
		
		private final Stack<ArrayList<Object>> listStack = new Stack<>();

		@Override
		public void beginObject() {
			Object object = instantiationStack.peek();
			if (!objectEntry.isEmpty()) {
				try {
					System.out.println(object.getClass());
					System.out.println(objectEntry.peek());
					Field field = object.getClass().getDeclaredField(objectEntry.peek());
					if (field.getType().isArray()) {
						listStack.push(new ArrayList<>());
						instantiationStack.push(field.getType().getComponentType().newInstance());
					} else {
						instantiationStack.push(field.getType().newInstance());
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | InstantiationException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		@Override
		public void endObject() {
			if (instantiationStack.size() > 1) {
				Object object = instantiationStack.pop();
				Object o = instantiationStack.peek();
				System.out.println(o.getClass());
				try {
					Field field = o.getClass().getDeclaredField(objectEntry.pop());
					field.setAccessible(true);
					if (field.getType().isArray()) {
						listStack.peek().add(object);
					} else {
						field.set(o, object);			
					}
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		@Override
		public void booleanLiteral(boolean value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				if (field.getType().isArray()) {
					if (field.getName().equals("java.lang.Boolean")) {
						listStack.peek().add(Boolean.valueOf(value));
					}
					else if (field.getName().equals("boolean")) {
						listStack.peek().add(value);
					}
				} else {
					if (field.getName().equals("java.lang.Boolean")) {
						field.set(object, Boolean.valueOf(value));
					}
					else if (field.getName().equals("boolean")) {
						field.setBoolean(object, value);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void doubleLiteral(double value) {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				if (field.getType().isArray()) {
					if (field.getType().getComponentType().getName().equals("java.lang.Float")) {
						listStack.peek().add(Float.valueOf((float) value));
					}
					else if (field.getType().getComponentType().getName().equals("java.lang.Double")) {
						listStack.peek().add(Double.valueOf(value));
					}
					else if (field.getType().getComponentType().getName().equals("float")) {
						listStack.peek().add((float) value);
					}
					else if (field.getType().getComponentType().getName().equals("double")) {
						listStack.peek().add(value);
					}
				} else {
					if (field.getType().getName().equals("java.lang.Float")) {
						object.getClass().getDeclaredField(objectEntry.pop()).set(object, Float.valueOf((float) value));
					}
					else if (field.getType().getName().equals("java.lang.Double")) {
						field.set(object, Double.valueOf(value));
					}
					else if (field.getGenericType().getTypeName().equals("float")) {
						field.setFloat(object, (float) value);
					}
					else if (field.getGenericType().getTypeName().equals("double")) {
						field.setDouble(object, value);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void longLiteral(long value) {
			Object object = instantiationStack.peek();
			try {
				System.out.println(object.getClass());
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				if (field.getType().isArray()) {
					if (field.getType().getComponentType().getName().equals("java.lang.Integer")) {
						listStack.peek().add(Integer.valueOf((int) value));
					}
					else if (field.getType().getComponentType().getName().equals("java.lang.Long")) {
						listStack.peek().add(Long.valueOf(value));
					}
					else if (field.getType().getComponentType().getTypeName().equals("int")) {
						listStack.peek().add((int) value);
					}
					else if (field.getType().getComponentType().getTypeName().equals("long")) {
						listStack.peek().add(value);
					}
				} else {
					if (field.getType().getName().equals("java.lang.Integer")) {
						field.set(object, Integer.valueOf((int) value));
					}
					else if (field.getType().getName().equals("java.lang.Long")) {
						field.set(object, Long.valueOf(value));
					}
					else if (field.getGenericType().getTypeName().equals("int")) {
						field.setInt(object, (int) value);
					}
					else if (field.getGenericType().getTypeName().equals("long")) {
						field.setLong(object, value);
					}
					else if (field.getType().getComponentType().getName().equals("java.lang.Byte")) {
						field.set(object, Byte.valueOf((byte) value));
					}
					else if (field.getType().getComponentType().getName().equals("java.lang.Short")) {
						field.set(object, Short.valueOf((short) value));
					}
					else if (field.getType().getComponentType().getName().equals("byte")) {
						field.setByte(object, (byte) value);
					}
					else if (field.getType().getComponentType().getName().equals("short")) {
						field.setShort(object, (short) value);
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void stringLiteral(String value) {
			System.out.println(value);
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				if (field.getType().isArray()) {
					listStack.peek().add(value);
				} else {
					field.set(object, value);	
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void nullLiteral() {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				if (field.getType().isArray()) {
					listStack.peek().add(null);
				} else {
					field.set(object, null);
				}
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void beginObjectEntry(String value) {
			objectEntry.push(value);
		}

		@Override
		public void beginList() {
			listStack.push(new ArrayList<>());
		}

		@Override
		public void endList() {
			Object object = instantiationStack.peek();
			try {
				Field field = object.getClass().getDeclaredField(objectEntry.peek());
				field.setAccessible(true);
				ArrayList<Object> list = listStack.pop();
				Object arr = Array.newInstance(field.getType().getComponentType(), list.size());
				toArray(list, field.getType().getComponentType(), arr);
				field.set(object, arr);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
		}
		
		public Object toBean() {
			return instantiationStack.peek();
		}
		
	}
	
	public static class BeanWriter {
		
		private StringBuffer buffer = new StringBuffer();
		
		public void write(Object bean) {
			if (bean == null) {
				buffer.append("null");
				return;
			}
			if (isLiteral(bean.getClass())) {
				writeLiteral(bean);
				return;
			}
			buffer.append("{");
			Field[] fields = bean.getClass().getDeclaredFields();
			boolean written = false;
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				System.out.println(field.getName());
				if (written) {
					buffer.append(",");
				}
				written = true;
				buffer.append("\"" + field.getName() + "\": ");
				try {
					if (field.getType().isArray()) {
						writeArray(field.get(bean));
					} else {
						if (isLiteral(field.getType())) {
							writeLiteral(field.get(bean));
						} else {
							write(field.get(bean));
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			buffer.append("}");
		}
		
		public boolean isLiteral(Class<?> type) {
			return     (type.getName().equals("java.lang.Character")) || (type.getName().equals("char")) 
					|| (type.getName().equals("java.lang.Boolean")) || (type.getName().equals("boolean")) 
					|| (type.getName().equals("java.lang.Byte")) || (type.getName().equals("byte")) 
					|| (type.getName().equals("java.lang.Short")) || (type.getName().equals("short"))
					|| (type.getName().equals("java.lang.Float")) || (type.getName().equals("float")) 
					|| (type.getName().equals("java.lang.Integer")) || (type.getName().equals("int")) 
					|| (type.getName().equals("java.lang.Double")) || (type.getName().equals("double"))
					|| (type.getName().equals("java.lang.Long")) || (type.getName().equals("long"))
					|| (type.getName().equals("java.lang.String"));
		}
		
		public void writeLiteral(Object value) {
			if (value.getClass().getName().equals("java.lang.Character")) {
				buffer.append((Character) value);
			}
			if (value.getClass().getName().equals("char")) {
				buffer.append((char) value);
			}
			if (value.getClass().getName().equals("java.lang.Boolean")) {
				buffer.append((Boolean) value ? "true" : "false");
			}
			else if (value.getClass().getName().equals("boolean")) {
				buffer.append((Boolean) value ? "true" : "false");
			}
			else if (value.getClass().getName().equals("java.lang.Byte")) {
				buffer.append(Byte.toString((Byte) value));
			}
			else if (value.getClass().getName().equals("byte")) {
				buffer.append(Byte.toString((Byte) value));
			}
			else if (value.getClass().getName().equals("java.lang.Short")) {
				buffer.append(Short.toString((Short) value));
			}
			else if (value.getClass().getName().equals("short")) {
				buffer.append(Short.toString((Short) value));
			}
			else if (value.getClass().getName().equals("java.lang.Float")) {
				buffer.append(Float.toString((Float) value));
			}
			else if (value.getClass().getName().equals("float")) {
				buffer.append(Float.toString((Float) value));
			}
			else if (value.getClass().getName().equals("java.lang.Integer")) {
				buffer.append(Integer.toString((Integer) value));
			}
			else if (value.getClass().getName().equals("int")) {
				buffer.append(Integer.toString((Integer) value));
			}
			else if (value.getClass().getName().equals("java.lang.Double")) {
				buffer.append(Double.toString((Double) value));
			}
			else if (value.getClass().getName().equals("double")) {
				buffer.append(Double.toString((Double) value));
			}
			else if (value.getClass().getName().equals("java.lang.Long")) {
				buffer.append(Long.toString((Long) value));
			}
			else if (value.getClass().getName().equals("long")) {
				buffer.append(Long.toString((Long) value));
			}
			else if (value.getClass().getName().equals("java.lang.String")) {
				buffer.append("\"" + (String) value + "\"");
			}
		}
		
		public void writeArray(Object array) {
			buffer.append("[");
			if (isLiteral(array.getClass().getComponentType())) {
				if (array.getClass().getComponentType().getName().equals("java.lang.Boolean")) {
					Boolean[] bools = (Boolean[]) array;
					for (int i = 0; i < bools.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(bools[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("boolean")) {
					boolean[] bools = (boolean[]) array;
					for (int i = 0; i < bools.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(bools[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Byte")) {
					Byte[] bytes = (Byte[]) array;
					for (int i = 0; i < bytes.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(bytes[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("byte")) {
					byte[] bytes = (byte[]) array;
					for (int i = 0; i < bytes.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(bytes[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Short")) {
					Short[] shorts = (Short[]) array;
					for (int i = 0; i < shorts.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(shorts[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("short")) {
					short[] shorts = (short[]) array;
					for (int i = 0; i < shorts.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(shorts[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Float")) {
					Float[] floats = (Float[]) array;
					for (int i = 0; i < floats.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(floats[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("float")) {
					float[] floats = (float[]) array;
					for (int i = 0; i < floats.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(floats[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Integer")) {
					Integer[] ints = (Integer[]) array;
					for (int i = 0; i < ints.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(ints[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("int")) {
					int[] ints = (int[]) array;
					for (int i = 0; i < ints.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(ints[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Double")) {
					Double[] doubles = (Double[]) array;
					for (int i = 0; i < doubles.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(doubles[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("double")) {
					double[] doubles = (double[]) array;
					for (int i = 0; i < doubles.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(doubles[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.Long")) {
					Long[] longs = (Long[]) array;
					for (int i = 0; i < longs.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(longs[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("long")) {
					long[] longs = (long[]) array;
					for (int i = 0; i < longs.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(longs[i]);
					}
				}
				else if (array.getClass().getComponentType().getName().equals("java.lang.String")) {
					String[] strings = (String[]) array;
					for (int i = 0; i < strings.length; i++) {
						if (i > 0) {
							buffer.append(",");
						}
						writeLiteral(strings[i]);
					}
				}
			} else {
				Object[] arr = (Object[]) array;
				for (int i = 0; i < arr.length; i++) {
					if (i > 0) {
						buffer.append(",");
					}
					write(arr[i]);
				}
			}
			buffer.append("]");
		}
		
		public CharSequence toText() {
			return buffer;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T readBean(char[] letters, Class<T> type, ClassConstructor constructor) {
		if (constructor == null) {
			constructor = (t) -> {
				try {
					return t.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			};
		}
		BeanReader reader = new BeanReader();
		try {
			reader.instantiationStack.push(type.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		ParserC.json(letters, reader);
		return (T) reader.toBean();
	}
	
	public static char[] writeBean(Object bean) {
		BeanWriter writer = new BeanWriter();
		writer.write(bean);
		CharSequence sequence = writer.toText();
		char[] letters = new char[sequence.length()];
		for (int i = 0; i < letters.length; i++) {
			letters[i] = sequence.charAt(i);
		}
		return letters;
		
	}

}
