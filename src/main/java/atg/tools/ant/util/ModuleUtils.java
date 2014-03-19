package atg.tools.ant.util;

import atg.tools.ant.types.ModuleCollection;
import org.apache.tools.ant.taskdefs.Manifest;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author msicker
 * @version 2.0
 */
public final class ModuleUtils {

    private ModuleUtils() {
    }

    public static final String ATG_REQUIRED = "ATG-Required";

    public static final String ATG_CLASS_PATH = "ATG-Class-Path";

    public static final String ATG_CONFIG_PATH = "ATG-Config-Path";

    private static final Pattern MODULE_SEPARATOR = Pattern.compile("\\.");

    public static String moduleNameToPath(final String moduleName) {
        return MODULE_SEPARATOR.matcher(moduleName).replaceAll(File.separator);
    }

    public static String joinResourceNames(final char join, final ResourceCollection resources) {
        final StringBuilder sb = new StringBuilder();
        // in order to support older versions of ant, we can't assume ResourceCollection is Iterable
        final Iterator<Resource> iterator = resources.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()) {
            sb.append(iterator.next().getName()).append(join);
        }
        if (sb.charAt(sb.length() - 1) == join) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Manifest.Attribute generateAtgRequiredAttribute(final ModuleCollection modules) {
        return new Manifest.Attribute(
                ATG_REQUIRED, joinResourceNames(' ', modules)
        );
    }

    public static Manifest.Attribute generateAtgClassPathAttribute(final Path classpath) {
        return new Manifest.Attribute(
                ATG_CLASS_PATH, joinResourceNames(' ', classpath)
        );
    }

    public static Manifest.Attribute generateAtgConfigPathAttribute(final Path configpath) {
        return new Manifest.Attribute(
                ATG_CONFIG_PATH, joinResourceNames(' ', configpath)
        );
    }
}
