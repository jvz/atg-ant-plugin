package atg.tools.ant.util;

import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.apache.tools.ant.taskdefs.Manifest.Attribute;

/**
 * Manifest file attributes specific to ATG.
 *
 * @author msicker
 * @version 2.0
 */
public enum AtgAttribute {
    ATG_REQUIRED("ATG-Required"), ATG_CLASS_PATH("ATG-Class-Path"), ATG_CONFIG_PATH("ATG-Config-Path");

    private final Attributes.Name name;

    AtgAttribute(final String name) {
        this.name = new Attributes.Name(name);
    }

    public Attributes.Name getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    /**
     * Gets the value of this manifest attribute from a given manifest instance.
     *
     * @param manifest instance of manifest to get attribute value from.
     *
     * @return value of attribute if existing, or {@code null} if the attribute isn't defined.
     *
     * @throws java.lang.NullPointerException if {@code manifest} is {@code null}.
     */
    public String extractValueFrom(final Manifest manifest) {
        if (manifest == null) {
            throw new NullPointerException("Provided manifest was null.");
        }
        return manifest.getMainAttributes().getValue(getName());
    }

    /**
     * Creates a new manifest attribute for use with ant's manifest task.
     *
     * @param value value to use for attribute named by this.
     *
     * @return an attribute name/value pair for use with ant.
     */
    public Attribute create(final String value) {
        return new Attribute(toString(), value);
    }

    public <T> Attribute using(final Iterator<T> iterator, final FeatureExtractor<T, String> extractor) {
        return create(StringUtils.join(' ', iterator, extractor));
    }
}
