/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.blobcity.db.exceptions.InvalidCredentialsException;
import com.blobcity.db.exceptions.InvalidEntityException;
import com.blobcity.db.exceptions.InvalidFieldException;
import com.blobcity.db.exceptions.OperationFailed;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Karishma
 */
public class NewEmptyJUnitTest {

    
    public NewEmptyJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void select_all() {
        SamplePOJO_3 p3 = new SamplePOJO_3();
        
        try {
            assertEquals(p3.selectAll(), true);
        } catch (InvalidCredentialsException ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidEntityException ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidFieldException ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OperationFailed ex) {
            Logger.getLogger(NewEmptyJUnitTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
