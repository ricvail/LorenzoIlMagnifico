package it.polimi.ingsw.pc42;

import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ResourceBonusTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ResourceBonusTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ResourceBonusTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAdd() {

        ResourceWrapper w = new ResourceWrapper(ResourceType.COIN, 10);

        w.addBonus(3);

        w.addUsingBonus(3);

        assertEquals(13, w.get());

        w.addUsingBonus(-1);
        assertEquals(13, w.get());

        w.addUsingBonus(-5);
        assertEquals(10, w.get());

    }

    public void testUndoAddAfterBonusUsed(){
        ResourceWrapper w = new ResourceWrapper(ResourceType.COIN, 10);
        w.addBonus(3);
        w.addUsingBonus(-5);

        assertEquals(8, w.get());


        w.abortAddUsingBonus(-1);
        assertEquals(8, w.get());

        w.abortAddUsingBonus(-2);
        assertEquals(8, w.get());

        w.abortAddUsingBonus(-2);
        assertEquals(10, w.get());

        w.addBonus(5);
        w.addUsingBonus(-3);
        assertEquals(10, w.get());

        w.abortAddUsingBonus(-3);
        assertEquals(10, w.get());

    }

}
