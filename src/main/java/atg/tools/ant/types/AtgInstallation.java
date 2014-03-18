package atg.tools.ant.types;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import atg.tools.ant.util.ModuleUtils;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * @author msicker
 * @version 2.0
 */
public class AtgInstallation
		extends FileResource {

	private final Map<String, Module> modules = new ConcurrentHashMap<String, Module>();

	/**
	 * Sets the base ATG installation directory. For example, one might have ATG installed in
	 * {@code $HOME/ATG/ATG11.0}, so you would use the following:
	 * <pre>
	 *     {@code <atg:atg id="atg.installation" home="${user.home}/ATG/ATG11.0"/>}
	 * </pre>
	 *
	 * @param home directory where ATG is installed.
	 */
	public void setHome( final File home ) {
		setFile( home );
	}

	public File getHome() {
		return getFile();
	}

	protected Module getModule( final String name ) {
		final Module cached = modules.get( name );
		if( cached != null ) {
			return cached;
		}
		final Module module = new Module();
		module.setAtg( this );
		module.setModule( ModuleUtils.moduleNameToPath( name ) );
		modules.put( name, module );
		return module;
	}
}
