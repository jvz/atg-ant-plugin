package atg.tools.ant.util;

import java.io.File;
import java.util.regex.Pattern;

/**
 * @author msicker
 * @version 2.0
 */
public final class ModuleUtils {

	private ModuleUtils() {}

	private static final Pattern MODULE_SEPARATOR = Pattern.compile( "\\." );

	public static String moduleNameToPath(final String moduleName) {
		return MODULE_SEPARATOR.matcher( moduleName ).replaceAll( File.separator );
	}

}
