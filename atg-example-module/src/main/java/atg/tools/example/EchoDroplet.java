package atg.tools.example;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author msicker
 * @version 1.0.0
 */
public class EchoDroplet
        extends DynamoServlet {

    private static final ParameterName INPUT = ParameterName.getParameterName("input");

    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    @Override
    protected void doGet(final DynamoHttpServletRequest req, final DynamoHttpServletResponse res)
            throws ServletException, IOException {
        final String input = req.getParameter(INPUT);
        req.setParameter("data", input);
        req.serviceLocalParameter(OUTPUT, req, res);
    }
}
