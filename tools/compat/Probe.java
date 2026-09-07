/**
 * Shared helper for the compatibility probes.
 *
 * Probe output is diffed between two builds, so it must be pure ASCII: a raw
 * control character in the stream makes diff treat the file as binary and skip
 * the line-by-line comparison entirely, silently hiding real differences.
 */
public class Probe {

    /** Render a value so that every character is printable and unambiguous. */
    public static String safe(Object value) {
        if (value == null) {
            return "null";
        }
        String s = String.valueOf(value);
        StringBuffer sb = new StringBuffer(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0x20 && c <= 0x7e) {
                sb.append(c);
            } else {
                sb.append("\\u");
                String hex = Integer.toHexString(c);
                for (int k = hex.length(); k < 4; k++) {
                    sb.append('0');
                }
                sb.append(hex);
            }
        }
        return sb.toString();
    }

    /** safe(), plus the runtime type, which is half of what compatibility means here. */
    public static String typed(Object value) {
        if (value == null) {
            return "null(null)";
        }
        return value.getClass().getSimpleName() + "(" + safe(value) + ")";
    }
}
