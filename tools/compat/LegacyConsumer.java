import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.util.*;

/**
 * Stands in for a downstream project written around 2012, calling json-simple
 * the way the API was idiomatically used at the time: raw types, the deprecated
 * parse(), and the interfaces implemented by hand.
 *
 * Compiled against the baseline release and then run against both builds, so any
 * binary-compatibility break shows up as a difference in its output.
 */
public class LegacyConsumer {

    // Common downstream pattern 1: implement ContainerFactory. Note the
    // misspelled method name - it is part of the published interface.
    static class OrderedFactory implements ContainerFactory {
        public Map createObjectContainer() { return new LinkedHashMap(); }
        public List creatArrayContainer()  { return new ArrayList(); }
    }

    // Common downstream pattern 2: implement ContentHandler for streaming.
    static class Counter implements ContentHandler {
        int objects, arrays, primitives;
        public void startJSON() {}
        public void endJSON() {}
        public boolean startObject()  { objects++; return true; }
        public boolean endObject()    { return true; }
        public boolean startObjectEntry(String key) { return true; }
        public boolean endObjectEntry() { return true; }
        public boolean startArray()   { arrays++; return true; }
        public boolean endArray()     { return true; }
        public boolean primitive(Object v) { primitives++; return true; }
    }

    // Common downstream pattern 3: implement JSONAware / JSONStreamAware.
    static class Money implements JSONAware, JSONStreamAware {
        long cents;
        Money(long c) { cents = c; }
        public String toJSONString() { return "{\"cents\":" + cents + "}"; }
        public void writeJSONString(Writer out) throws IOException { out.write(toJSONString()); }
    }

    // Common downstream pattern 4: subclass JSONObject, using raw types.
    static class Config extends JSONObject {
        Config() { super(); put("kind", "config"); }
    }

    static void p(String label, Object v) { System.out.println(label + " = " + Probe.safe(v)); }

    public static void main(String[] args) throws Exception {
        String doc = "{\"id\":42,\"ratio\":0.5,\"ok\":true,\"none\":null,"
                   + "\"path\":\"a/b\",\"list\":[1,\"two\",3.0,false,null],"
                   + "\"nested\":{\"x\":{\"y\":[{\"z\":1}]}}}";

        // Decoding, and the decoded-type contract.
        JSONObject o = (JSONObject) JSONValue.parseWithException(doc);
        p("id",     o.get("id")     + " : " + o.get("id").getClass().getName());
        p("ratio",  o.get("ratio")  + " : " + o.get("ratio").getClass().getName());
        p("ok",     o.get("ok")     + " : " + o.get("ok").getClass().getName());
        p("none",   o.get("none"));
        p("path",   o.get("path"));
        JSONArray arr = (JSONArray) o.get("list");
        p("list.size", arr.size());
        p("list[1]",   arr.get(1));
        p("nested",    JSONValue.toJSONString(o.get("nested")));

        // The deprecated parse(): returns null rather than reporting an error.
        p("parse(garbage)", JSONValue.parse("{"));
        p("parse(ok)",      JSONValue.parse("[1,2]"));

        // Encoding: maps, lists, arrays, and a custom JSONAware type.
        Map m = new LinkedHashMap();
        m.put("s", "a/b "); m.put("n", new Integer(7)); m.put("d", new Double(1.5));
        m.put("arr", new int[]{1,2,3}); m.put("bytes", new byte[]{-1,2});
        m.put("chars", new char[]{'x','y'}); m.put("money", new Money(999));
        p("encode map", JSONValue.toJSONString(m));

        JSONArray ja = new JSONArray();
        ja.add("q"); ja.add(new Money(1)); ja.add(null);
        p("encode JSONArray", ja.toJSONString());
        p("JSONArray.toString", ja.toString());

        Config cfg = new Config();
        p("subclass", cfg.toJSONString());

        // Static encoding helpers.
        p("JSONObject.toString(k,v)", JSONObject.toString("k", "v/1"));
        p("JSONObject.escape", JSONObject.escape("a/b\t "));
        p("JSONValue.escape",  JSONValue.escape("</script>"));

        // ContainerFactory
        JSONParser parser = new JSONParser();
        Map ordered = (Map) parser.parse("{\"z\":1,\"a\":2,\"m\":3}", new OrderedFactory());
        p("ordered keys", ordered.keySet());

        // ContentHandler
        Counter c = new Counter();
        new JSONParser().parse(new StringReader(doc), c);
        p("counts", c.objects + "/" + c.arrays + "/" + c.primitives);

        // ParseException details.
        try { new JSONParser().parse("[1,2}"); }
        catch (ParseException e) {
            p("PE.message", e.getMessage());
            p("PE.position", e.getPosition());
            p("PE.errorType", e.getErrorType());
            p("PE.unexpected", e.getUnexpectedObject());
        }

        // Java serialization of JSONObject - downstream code puts these into
        // HTTP sessions and caches.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos); oos.writeObject(o); oos.close();
        JSONObject back = (JSONObject) new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())).readObject();
        p("serialize roundtrip", back.get("id") + "," + back.get("path"));

        // ItemList
        ItemList il = new ItemList("a,b,c");
        p("ItemList", il.size() + " " + il.get(0) + " " + il.toString());

        // Streaming write.
        StringWriter sw = new StringWriter();
        JSONValue.writeJSONString(o.get("list"), sw);
        p("writeJSONString", sw.toString());
    }
}
