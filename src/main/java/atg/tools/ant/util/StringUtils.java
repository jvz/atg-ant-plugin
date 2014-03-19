package atg.tools.ant.util;

/**
 * @author msicker
 * @version 2.0
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String trimToEmpty(final String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean isBlank(final String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(final String s) {
        return !isBlank(s);
    }

}
