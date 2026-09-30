package org.json.simple;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;

import junit.framework.TestCase;

public class ConformanceTest extends TestCase {

	private static final String[] DOCUMENTS = {
		"{}",
		"[]",
		"null",
		"true",
		"false",
		"0",
		"-1",
		"42",
		"1.5",
		"1.50",
		"-0.0",
		"1e10",
		"1E-10",
		"1.7976931348623157e308",
		"9223372036854775807",
		"-9223372036854775808",
		"\"\"",
		"\"plain\"",
		"\"a/b\"",
		"\"</script>\"",
		"\"tab\\there\"",
		"\"newline\\nhere\"",
		"\"quote\\\"here\"",
		"\"backslash\\\\here\"",
		"\"\\b\\f\\r\"",
		"\"\\u0001\"",
		"\"\\u007f\"",
		"\"\\u0085\"",
		"\"\\u2028\\u2029\"",
		"\"\\u20ac\"",
		"\"\\ud83d\\ude00\"",
		"\"caf\u00e9\"",
		"[1,2,3]",
		"[\"a\",\"b\"]",
		"[null,true,false]",
		"[[],[[]],[[[]]]]",
		"[{},{\"a\":1}]",
		"{\"a\":1}",
		"{\"\":1}",
		"{\"a\":null}",
		"{\"a\":\"/\"}",
		"{\"a\":{\"b\":{\"c\":[1,2,{\"d\":null}]}}}",
		"{\"one\":1,\"two\":2,\"three\":3,\"four\":4,\"five\":5}",
		"  {  \"a\"  :  [ 1 , 2 ]  }  ",
		"{\"unicode\":\"\\u4e2d\\u6587\",\"emoji\":\"\\ud83c\\udf89\"}",
	};

	public void testDecodingAgreesWithTheReference() throws Exception {
		for (int i = 0; i < DOCUMENTS.length; i++) {
			String document = DOCUMENTS[i];
			assertEquivalent(document,
					JSONValue.parseWithException(document),
					reference(document));
		}
	}

	public void testEncodingIsReadableByTheReference() throws Exception {
		for (int i = 0; i < DOCUMENTS.length; i++) {
			String document = DOCUMENTS[i];
			String encoded = JSONValue.toJSONString(JSONValue.parseWithException(document));
			assertEquivalent(document + " -> " + encoded,
					JSONValue.parseWithException(document),
					reference(encoded));
		}
	}

	public void testReferenceOutputIsReadableByJsonSimple() throws Exception {
		for (int i = 0; i < DOCUMENTS.length; i++) {
			String document = isContainer(DOCUMENTS[i])
					? DOCUMENTS[i]
					: "[" + DOCUMENTS[i] + "]";
			String encoded = String.valueOf(reference(document));
			assertEquivalent(document + " <- " + encoded,
					JSONValue.parseWithException(encoded),
					reference(document));
		}
	}

	public void testEncodedOutputHasNoRawControlCharacters() throws Exception {
		for (int i = 0; i < DOCUMENTS.length; i++) {
			String encoded = JSONValue.toJSONString(
					JSONValue.parseWithException(DOCUMENTS[i]));
			for (int at = 0; at < encoded.length(); at++) {
				char c = encoded.charAt(at);
				assertTrue("unescaped control character U+"
								+ Integer.toHexString(c) + " at " + at
								+ " of " + DOCUMENTS[i] + " -> " + encoded,
						c >= 0x20);
			}
		}
	}

	public void testIntegersBeyondLongAreNotYetSupported() {
		String document = "123456789012345678901234567890";

		assertEquals(java.math.BigInteger.class, reference(document).getClass());

		try {
			JSONValue.parseWithException(document);
			fail("json-simple now reads integers beyond long; update this test "
					+ "and the note in README.md");
		} catch (ParseException expected) {
			assertEquals(ParseException.ERROR_UNEXPECTED_EXCEPTION, expected.getErrorType());
		}
	}


	private static Object reference(String document) {
		return new org.json.JSONTokener(document).nextValue();
	}

	private static boolean isContainer(String document) {
		String trimmed = document.trim();
		return trimmed.startsWith("{") || trimmed.startsWith("[");
	}

	private static void assertEquivalent(String context, Object mine, Object theirs) {
		if (mine == null || org.json.JSONObject.NULL.equals(theirs)) {
			assertTrue(context + ": one side is null, the other is " + mine + " / " + theirs,
					mine == null && org.json.JSONObject.NULL.equals(theirs));
			return;
		}

		if (mine instanceof Map) {
			assertTrue(context + ": expected an object, got " + theirs.getClass().getName(),
					theirs instanceof org.json.JSONObject);
			Map mineMap = (Map) mine;
			org.json.JSONObject theirsObject = (org.json.JSONObject) theirs;
			assertEquals(context + ": different number of members",
					mineMap.size(), theirsObject.length());
			for (Iterator it = mineMap.keySet().iterator(); it.hasNext();) {
				String key = String.valueOf(it.next());
				assertTrue(context + ": the reference has no member " + key,
						theirsObject.has(key));
				assertEquivalent(context + "." + key, mineMap.get(key), theirsObject.get(key));
			}
			return;
		}

		if (mine instanceof List) {
			assertTrue(context + ": expected an array, got " + theirs.getClass().getName(),
					theirs instanceof org.json.JSONArray);
			List mineList = (List) mine;
			org.json.JSONArray theirsArray = (org.json.JSONArray) theirs;
			assertEquals(context + ": different number of elements",
					mineList.size(), theirsArray.length());
			for (int i = 0; i < mineList.size(); i++) {
				assertEquivalent(context + "[" + i + "]", mineList.get(i), theirsArray.get(i));
			}
			return;
		}

		if (mine instanceof Number) {
			assertTrue(context + ": expected a number, got " + theirs.getClass().getName(),
					theirs instanceof Number);
			assertEquals(context + ": " + mine + " != " + theirs, 0,
					new BigDecimal(mine.toString()).compareTo(new BigDecimal(theirs.toString())));
			return;
		}

		assertEquals(context, mine, theirs);
	}
}
