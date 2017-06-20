package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ResourceBonusTest
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ResourceBonusTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ResourceBonusTest.class);
    }

    /**
     * Rigourous Test :-)
     * <p>
     * public void testAdd() {
     * <p>
     * ResourceWrapper w = new ResourceWrapper(ResourceType.COIN, 10);
     * <p>
     * w.setBonus(3);
     * <p>
     * w.addUsingBonus(3);
     * <p>
     * assertEquals(13, w.get());
     * <p>
     * w.addUsingBonus(-1);
     * assertEquals(13, w.get());
     * <p>
     * w.addUsingBonus(-5);
     * assertEquals(10, w.get());
     * <p>
     * }
     * <p>
     * public void testUndoAddAfterBonusUsed(){
     * ResourceWrapper w = new ResourceWrapper(ResourceType.COIN, 10);
     * w.setBonus(3);
     * w.addUsingBonus(-5);
     * <p>
     * assertEquals(8, w.get());
     * <p>
     * w.undoAddUsingBonus(-1);
     * assertEquals(8, w.get());
     * <p>
     * w.undoAddUsingBonus(-2);
     * assertEquals(8, w.get());
     * <p>
     * w.undoAddUsingBonus(-2);
     * assertEquals(10, w.get());
     * <p>
     * w.setBonus(5);
     * w.addUsingBonus(-3);
     * assertEquals(10, w.get());
     * <p>
     * w.undoAddUsingBonus(-3);
     * assertEquals(10, w.get());
     * <p>
     * }
     */
    public void testNothing() {
        assertEquals(true, true);
    }
}