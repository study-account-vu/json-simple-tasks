import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

/**
 * Command-line harness for checking serialized forms across library builds.
 * Supports serial-version inspection, file-based read/write, and an in-memory
 * round trip.
 */
public class SerCheck {

    /** Representative JSON document used to produce the serialized object. */
    private static final String DOC =
        "{\"id\":42,\"s\":\"a/b\",\"l\":[1,2],\"n\":null,\"d\":1.5,\"b\":true}";

    /**
     * Runs the selected serialization check.
     *
     * @param args mode, followed where applicable by the serialized-file path;
     *             supported modes are {@code svuid}, {@code write}, and
     *             {@code read}
     * @throws Exception if class loading, serialization, or file access fails
     */
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "svuid";

        if ("svuid".equals(mode)) {
            String[] types = {
                "org.json.simple.JSONObject",
                "org.json.simple.JSONArray",
                "org.json.simple.parser.ParseException",
                "org.json.simple.parser.Yytoken",
            };
            for (int i = 0; i < types.length; i++) {
                ObjectStreamClass osc = ObjectStreamClass.lookup(Class.forName(types[i]));
                System.out.println(types[i] + " -> "
                    + (osc == null ? "not serializable" : String.valueOf(osc.getSerialVersionUID())));
            }
            return;
        }

        File file = new File(args[1]);

        if ("write".equals(mode)) {
            JSONObject obj = (JSONObject) JSONValue.parse(DOC);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(obj);
            out.close();
            System.out.println("wrote " + file.getName());
            return;
        }

        if ("read".equals(mode)) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            JSONObject obj = (JSONObject) in.readObject();
            in.close();
            System.out.println("read " + file.getName()
                + " id=" + obj.get("id")
                + " s=" + obj.get("s")
                + " l=" + JSONValue.toJSONString(obj.get("l"))
                + " n=" + obj.get("n")
                + " d=" + obj.get("d")
                + " b=" + obj.get("b"));
            return;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(JSONValue.parse(DOC));
        out.close();
        Object back = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
        System.out.println("roundtrip " + ((JSONObject) back).get("id"));
    }
}
