/** Formats values for stable, human-readable compatibility probe output. */
public class Probe {

    /**
     * Converts a value to text and escapes characters outside printable ASCII.
     *
     * @param value value to display
     * @return printable representation, with non-ASCII code units rendered as
     *         four-digit Unicode escapes
     */
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

    /**
     * Formats a value with its runtime simple class name and safe text form.
     *
     * @param value value to format
     * @return type and value description, or {@code null(null)} for null
     */
    public static String typed(Object value) {
        if (value == null) {
            return "null(null)";
        }
        return value.getClass().getSimpleName() + "(" + safe(value) + ")";
    }
}
