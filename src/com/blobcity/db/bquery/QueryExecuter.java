
package com.blobcity.db.bquery;

import com.blobcity.db.exceptions.DbOperationException;
import com.blobcity.db.exceptions.ExceptionType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.json.JSONObject;

/**
 * Executes a Database query.
 * 
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class QueryExecuter {
    
    private static boolean EJB_AVAILABLE = false;
    private static final String JNDI_RESOURCE = "java:global/TestEAR/TEjb/Calculator!com.CalculatorRemote";
    private static InitialContext context;

    static {
        try /* Detect mode of operation */ {
            context = new InitialContext();
            
            //TODO: Set appropriate JNDI resource URL
            BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_RESOURCE);
            EJB_AVAILABLE = bean != null;
        } catch (NamingException ex) {
            //do nothing
        }
    }
    
    public String executeQuery(JSONObject queryJson) throws DbOperationException {
        if(EJB_AVAILABLE) {
            return executeEJB(queryJson);
        }else {
            return executeHTTP(queryJson);
        }
    }
    
    private String executeEJB(JSONObject jsonObject) throws DbOperationException {
        try {
            BQueryExecutorBeanRemote bean = (BQueryExecutorBeanRemote) context.lookup(JNDI_RESOURCE);
            return bean.runQuery(jsonObject.toString());
        } catch (NamingException ex) {
            throw new DbOperationException(ExceptionType.CONNECTION_ERROR, "Could not locate JNDI resource for communication with Database");
        }
    }
    
    private String executeHTTP(JSONObject jsonObject) {
        throw new UnsupportedOperationException("Not yet supported.");
    }
}
