package atg.tools.ant.util;

/**
 * Simple FeatureExtractor that doesn't do anything.
 *
 * @author msicker
 * @version 2.0
 */
public class IdentityFeatureExtractor<S>
        implements FeatureExtractor<S, S> {

    @Override
    public S extract(final S original) {
        return original;
    }
}
