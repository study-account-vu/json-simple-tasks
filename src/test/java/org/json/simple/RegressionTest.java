package org.json.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.Yytoken;

import junit.framework.TestCase;

public class RegressionTest extends TestCase {

	public void testOverflowingIntegerReportsParseException() throws Exception {
		try {
			JSONValue.parseWithException("99999999999999999999999999");
			fail("expected a ParseException");
		} catch (ParseException e) {
			assertEquals(ParseException.ERROR_UNEXPECTED_EXCEPTION, e.getErrorType());
			assertTrue(e.getUnexpectedObject() instanceof NumberFormatException);
		}
	}

	public void testItemListSeparatorConstructor() {
		ItemList list = new ItemList("a|b", "|");
		assertEquals(2, list.size());
		assertEquals("a", list.get(0));
		assertEquals("b", list.get(1));
		assertEquals("a|b", list.toString());
	}

	public void testParseExceptionIsSerializable() throws Exception {
		ParseException original = null;
		try {
			JSONValue.parseWithException("[1,2}");
			fail("expected a ParseException");
		} catch (ParseException e) {
			original = e;
		}
		assertTrue(original.getUnexpectedObject() instanceof Yytoken);

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(original);
		out.close();

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		ParseException restored = (ParseException) in.readObject();
		in.close();

		assertEquals(original.getMessage(), restored.getMessage());
		assertEquals(original.getPosition(), restored.getPosition());
	}


	public void testListOverloadsStillExist() throws Exception {
		assertNotNull(JSONArray.class.getMethod("toJSONString", java.util.List.class));
		assertNotNull(JSONArray.class.getMethod("writeJSONString",
				java.util.List.class, java.io.Writer.class));
		assertNotNull(JSONArray.class.getMethod("toJSONString", java.util.Collection.class));
		assertNotNull(JSONArray.class.getMethod("writeJSONString",
				java.util.Collection.class, java.io.Writer.class));

		java.util.List list = new java.util.ArrayList();
		list.add("x");
		list.add(Long.valueOf(1));
		assertEquals("[\"x\",1]", JSONArray.toJSONString(list));

		java.io.StringWriter writer = new java.io.StringWriter();
		JSONArray.writeJSONString(list, writer);
		assertEquals("[\"x\",1]", writer.toString());
	}

	public void testParseExceptionToStringCarriesTheDescription() {
		try {
			JSONValue.parseWithException("[1,2}");
			fail("expected a ParseException");
		} catch (ParseException e) {
			String expected = "Unexpected token RIGHT BRACE(}) at position 4.";
			assertEquals(expected, e.getMessage());
			assertEquals(expected, e.toString());
		}
	}

	public void testParseExceptionSerialVersionUidIsUnchanged() {
		assertEquals(-7880698968187728548L,
				java.io.ObjectStreamClass.lookup(ParseException.class).getSerialVersionUID());
	}


	public void testEscapingIsUnchanged() {
		assertEquals("\"a\\/b\"", JSONValue.toJSONString("a/b"));
		assertEquals("\"\\u2028\"", JSONValue.toJSONString("\u2028"));
		assertEquals("\"\\u0085\"", JSONValue.toJSONString("\u0085"));
		assertEquals("\"\\t\"", JSONValue.toJSONString("\t"));
	}

	public void testDecodedNumberTypesAreUnchanged() throws Exception {
		assertEquals(Long.class, JSONValue.parseWithException("1").getClass());
		assertEquals(Long.class, JSONValue.parseWithException("2147483648").getClass());
		assertEquals(Double.class, JSONValue.parseWithException("1.5").getClass());
		assertEquals(Double.class, JSONValue.parseWithException("1e3").getClass());
	}

	public void testDeprecatedParseStillReturnsNullOnGarbage() {
		assertNull(JSONValue.parse("{"));
		assertNull(JSONValue.parse("'nope'"));
	}

	public void testParserLeniencyIsUnchanged() throws Exception {
		assertEquals("[1,2]", JSONValue.toJSONString(JSONValue.parseWithException("[1,2,]")));
		assertEquals("[1,2]", JSONValue.toJSONString(JSONValue.parseWithException("[1 2]")));
		assertEquals("[1,2]", JSONValue.toJSONString(JSONValue.parseWithException("[1,,2]")));
		assertEquals("{\"a\":1}", JSONValue.toJSONString(JSONValue.parseWithException("{\"a\" 1}")));
		assertEquals(Long.valueOf(123), JSONValue.parseWithException("0123"));
	}

	public void testDeeplyNestedInputStillDecodes() throws Exception {
		int depth = 50000;
		StringBuilder sb = new StringBuilder(depth * 2);
		for (int i = 0; i < depth; i++) {
			sb.append('[');
		}
		for (int i = 0; i < depth; i++) {
			sb.append(']');
		}
		Object decoded = JSONValue.parseWithException(sb.toString());
		assertTrue(decoded instanceof JSONArray);
	}
}
