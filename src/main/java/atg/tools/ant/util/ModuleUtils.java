package atg.tools.ant.util;

import atg.tools.ant.types.Module;
import org.apache.tools.ant.types.Resource;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author msicker
 * @version 2.0
 */
public final class ModuleUtils {

    private ModuleUtils() {
    }

    public static final FeatureExtractor<Resource, String> RESOURCE_NAME_EXTRACTOR = new NameExtractor();

    public static final FeatureExtractor<Resource, String> MODULE_NAME_EXTRACTOR = new ModuleNameExtractor();

    private static final Pattern MODULE_SEPARATOR = Pattern.compile("\\.");

    public static String moduleNameToPath(final String moduleName) {
        return MODULE_SEPARATOR.matcher(moduleName).replaceAll(File.separator);
    }

    private static class NameExtractor
            implements FeatureExtractor<Resource, String> {

        @Override
        public String extract(final Resource original) {
            return original.getName();
        }
    }

    private static class ModuleNameExtractor
            implements FeatureExtractor<Resource, String> {

        @Override
        public String extract(final Resource original) {
            if (original instanceof Module) {
                return ((Module) original).getModule();
            } else {
                return null;
            }
        }
    }

}
