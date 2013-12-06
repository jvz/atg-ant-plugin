package atgant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;

public class AtgModuleName extends Task {

	private File atgHome;
	private String propertyName;
	private String moduleName;
	private File moduleDir;

	@Override
	public void execute() throws BuildException {
		if (atgHome == null) {
			throw new BuildException("no atgHome set");
		}
		AtgSystem atgSystem = new AtgSystem(atgHome, getProject().getBaseDir());

		AtgModule module;
		if (moduleName != null && moduleDir != null) {
			throw new BuildException("you can not set both a module= and a dir=");
		} else if (moduleName != null) {
			module = atgSystem.getModule(moduleName);
		} else if (moduleDir != null) {
			try {
				module = atgSystem.getModule(moduleDir);
			} catch (IOException e) {
				throw new BuildException("could not load module "+moduleDir, e);
			}
		} else {
			module = atgSystem.getModule(".");
		}
		getProject().setProperty(propertyName, module.getName());
	}

	public void setAtgHome(File atgHome) {
		this.atgHome = atgHome;
	}

	public void setProperty(String propertyName) {
		this.propertyName = propertyName;
	}

	public void setModule(String moduleName) {
		this.moduleName = moduleName;
	}

	public void setDir(File moduleDir) {
		this.moduleDir = moduleDir;
	}

}
