import org.json.simple.*; import org.json.simple.parser.*;
import java.io.*; import java.util.*;

/**
 * Runs a broad set of inputs through decode and then encode, printing a
 * fingerprint of every result so that two builds can be diffed line by line.
 */
public class Corpus {
    static String show(Object o) {
        if (o == null) return "null(null)";
        return o.getClass().getSimpleName() + "(" + Probe.safe(JSONValue.toJSONString(o)) + ")";
    }
    static void t(String in) {
        String r;
        try { r = show(JSONValue.parseWithException(in)); }
        catch (Throwable e) { r = "THROW " + e.getClass().getName(); }
        String r2;
        try { r2 = String.valueOf(JSONValue.parse(in)); }
        catch (Throwable e) { r2 = "THROW " + e.getClass().getName(); }
        System.out.println("[" + Probe.safe(in) + "] => " + Probe.safe(r) + " | parse=" + Probe.safe(r2));
    }
    static void e(Object v) {
        String r;
        try { r = JSONValue.toJSONString(v); } catch (Throwable ex) { r = "THROW " + ex.getClass().getName(); }
        System.out.println("enc(" + (v == null ? "null" : v.getClass().getSimpleName()) + ") => " + Probe.safe(r));
    }
    public static void main(String[] a) throws Exception {
        String[] inputs = {
          "{}", "[]", "null", "true", "false", "0", "-0", "1", "-1", "123456789012345678",
          "9223372036854775807", "9223372036854775808", "-9223372036854775809",
          "99999999999999999999999999", "0.0", "-0.0", "1.5", "1e3", "1E3", "1e-3", "1.5e+3",
          "1e999", "-1e999", "0123", "00", "\"\"", "\"a\"", "\"a/b\"", "\"\\u0041\"",
          "\"\\uZZZZ\"", "\"\\q\"", "\"\\\\\"", "\"\\\"\"", "\"\\b\\f\\n\\r\\t\"",
          "\"\\u2028\"", "\"\\u0000\"", "\"\\u007f\"", "\"\\u0085\"", "\"\\u2060\"",
          "\"\\ud83d\\ude00\"", "\"\\ud800\"",
          // Raw (unescaped) multi-byte UTF-8 input: two-byte U+00E9 and
          // three-byte U+03A9 / U+20AC. The escaped form is covered above.
          "\"café\"", "\"Ω\"", "\"€\"",
          "[1,2,3]", "[1,2,]", "[1 2]", "[,]", "[1,,2]", "[[[]]]", "[{},[],\"\"]",
          "{\"a\":1}", "{\"a\":1,}", "{\"a\":1 \"b\":2}", "{\"a\" 1}", "{,}",
          "{\"a\":1,\"a\":2}", "{\"\":1}", "{\"a\":{\"b\":{\"c\":[1,2,{\"d\":null}]}}}",
          "  [ 1 , 2 ]  ", "\t[1]\n", "", " ", "{", "[", "}", "]", ":", ",",
          "'x'", "undefined", "True", "NaN", "Infinity", "+1", ".5", "5.", "01.5",
          "[1,2] extra", "{\"a\":1}{\"b\":2}"
        };
        for (String s : inputs) t(s);

        // Encoding side.
        e(null); e("a/b"); e(Integer.valueOf(1)); e(Long.valueOf(1)); e(Short.valueOf((short)1));
        e(Byte.valueOf((byte)1)); e(Float.valueOf(1.5f)); e(Double.valueOf(1.5));
        e(Float.valueOf(Float.NaN)); e(Double.valueOf(Double.NaN));
        e(Float.valueOf(Float.POSITIVE_INFINITY)); e(Double.valueOf(Double.NEGATIVE_INFINITY));
        e(Boolean.TRUE); e(new java.math.BigDecimal("1.10")); e(new java.math.BigInteger("999999999999999999999"));
        e(new int[]{1,2}); e(new long[]{1L}); e(new short[]{1}); e(new byte[]{1});
        e(new float[]{1.5f}); e(new double[]{1.5}); e(new boolean[]{true}); e(new char[]{'a','/'});
        e(new String[]{"a","b"}); e(new Object[]{null,1,"x"});
        e(new int[0]); e(new Object[0]); e((int[]) null);
        e(new ArrayList()); e(new HashMap());
        e(Arrays.asList(1, null, "s")); e(new java.util.Date(0)); e(new StringBuffer("sb"));
        Map lhm = new LinkedHashMap(); lhm.put(null, 1); lhm.put(Integer.valueOf(2), "x"); lhm.put("k", null);
        e(lhm);
        e(new HashSet(Arrays.asList("only")));

        // Fingerprint of escape() over every character it special-cases, so a
        // change to the escaping rules cannot slip through unnoticed.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 0x2FFF; i++) sb.append(JSONValue.escape(String.valueOf((char) i)));
        System.out.println("escape-fingerprint-length=" + sb.length());
        System.out.println("escape-fingerprint-hash=" + sb.toString().hashCode());

        // ItemList. Two bugs here were fixed in 1.1.2, so differences are expected.
        System.out.println("ItemList(a,b,c).toString=" + Probe.safe(new ItemList("a,b,c").toString()));
        System.out.println("ItemList(a;b,';').size=" + new ItemList("a;b", ";").size());
    }
}
