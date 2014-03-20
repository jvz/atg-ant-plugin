package atg.tools.ant.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static atg.tools.ant.util.StringUtils.isBlank;
import static atg.tools.ant.util.StringUtils.isNotBlank;
import static atg.tools.ant.util.StringUtils.trimToEmpty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author msicker
 * @version 2.0
 */
@RunWith(Parameterized.class)
public class StringUtilsTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[][]{
                        { "\ta\t", "a", false },
                        { "", "", true },
                        { " ", "", true },
                        { "\t", "", true },
                        { "abc", "abc", false },
                        { " abc", "abc", false },
                        { "abc ", "abc", false },
                        { "\nabc\t\n\t", "abc", false },
                        { "\r\n", "", true },
                        { null, "", true }
                }
        );
    }

    private final String input;
    private final String expected;
    private final boolean blank;

    public StringUtilsTest(final String input, final String expected, final boolean blank) {
        this.input = input;
        this.expected = expected;
        this.blank = blank;
    }

    @Test
    public void testTrimToEmpty()
            throws Exception {
        assertThat(trimToEmpty(input), is(equalTo(expected)));
    }

    @Test
    public void testIsBlank()
            throws Exception {
        assertThat(isBlank(input), is(blank));
    }

    @Test
    public void testIsNotBlank()
            throws Exception {
        assertThat(isNotBlank(input), is(not(blank)));
    }
}
