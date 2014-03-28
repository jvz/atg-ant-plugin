package atg.tools.ant.util;

import java.util.Iterator;

/**
 * Various string utility classes. We don't need to import commons-lang just to use a couple functions, so we'll just
 * make our own!
 *
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

    public static <T> String join(final char c,
                                  final Iterator<? extends T> iterator,
                                  final FeatureExtractor<? super T, String> extractor) {
        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(extractor.extract(iterator.next())).append(c);
        }
        if (sb.length() == 0) {
            return "";
        }
        if (sb.charAt(sb.length() - 1) == c) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
