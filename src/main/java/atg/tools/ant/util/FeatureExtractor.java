package atg.tools.ant.util;

/**
 * Lambda function to extra some feature from another object.
 *
 * @param <S> type of object to extract feature from.
 * @param <T> type of feature to extract.
 *
 * @author msicker
 * @version 2.0
 */
public interface FeatureExtractor<S, T> {

    T extract(S original);

}
