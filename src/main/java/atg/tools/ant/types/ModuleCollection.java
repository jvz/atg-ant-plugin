package atg.tools.ant.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import atg.tools.ant.util.DependencyBuilder;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * @author msicker
 * @version 2.0
 */
public class ModuleCollection
		extends DataType
		implements ResourceCollection, Iterable<Resource> {
	// implements Iterable for convenience due to old versions of ant

	private final Collection<Resource> modules = new ArrayList<Resource>();

	private AtgInstallation atgInstallation;

	public void setAtg( final AtgInstallation atgInstallation ) {
		this.atgInstallation = atgInstallation;
	}

	public void setAtg( final Reference reference ) {
		setAtg( (AtgInstallation) reference.getReferencedObject() );
	}

	/**
	 * Creates a new nested module element.
	 *
	 * @return new module inheriting this ATG installation.
	 */
	public Module createModule() {
		final Module module = new Module();
		if( atgInstallation != null ) {
			module.setAtg( atgInstallation );
		}
		modules.add( module );
		return module;
	}

	/**
	 * Adds a module to this collection.
	 *
	 * @param module the module to add.
	 */
	public void add( final Module module ) {
		if( atgInstallation != null ) {
			module.setAtg( atgInstallation );
		}
		modules.add( module );
	}

	/**
	 * Calculates the full module dependency listing for this collection.
	 *
	 * @return expanded module collection of all the dependencies of this.
	 */
	public ModuleCollection getDependencies() {
		return new DependencyBuilder().withModules( this ).build();
	}

	@Override
	public Iterator<Resource> iterator() {
		return modules.iterator();
	}

	@Override
	public int size() {
		return modules.size();
	}

	@Override
	public boolean isFilesystemOnly() {
		return true;
	}

	public boolean contains( final Module module ) {
		return modules.contains( module );
	}
}
