package atgant;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.ResourceCollection;

public class AtgClasspath extends DataType implements ResourceCollection {
	
	private File atgHome;
	private String modules = ".";
	private List<File> classPathFileList;
	
	private void buildClassPathFileList() {
		if (classPathFileList == null) {
			if (atgHome == null) {
				throw new BuildException("no atgHome set");
			}
			AtgSystem atgSystem = new AtgSystem(atgHome, getProject().getBaseDir());
			classPathFileList = atgSystem.getClassPath(modules);
		}
	}
	
	public void setAtgHome(File atgHome) {
		this.atgHome = atgHome;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public boolean isFilesystemOnly() {
		return true; // Everything comes from the local file system
	}

	public int size() {
		buildClassPathFileList();
		return classPathFileList.size();
	}

	@SuppressWarnings("unchecked")
	public Iterator iterator() {
		buildClassPathFileList();
		return classPathFileList.iterator();
	}

}
