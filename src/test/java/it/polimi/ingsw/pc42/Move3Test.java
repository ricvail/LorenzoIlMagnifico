package it.polimi.ingsw.pc42;

        import com.fasterxml.jackson.databind.JsonNode;
        import it.polimi.ingsw.pc42.Control.ActionAbortedException;
        import it.polimi.ingsw.pc42.Control.ResourceType;
        import it.polimi.ingsw.pc42.Model.Board;
        import it.polimi.ingsw.pc42.Model.FamilyMember;
        import it.polimi.ingsw.pc42.Model.Player;
        import it.polimi.ingsw.pc42.Utilities.GameInitializer;
        import junit.framework.Test;
        import junit.framework.TestCase;
        import junit.framework.TestSuite;


public class Move3Test extends TestCase
{
    public Move3Test(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( Move3Test.class );
    }

    public void testMove3(){
        JsonNode mosse = GameInitializer.readFile("src/res/mosse_per_moveTest3.json").get("moves");
        JsonNode players = GameInitializer.readFile("src/res/prova_playerInit4.json");
        Board b = GameInitializer.initBaseGame(players, false);
        //RED, BLUE, YELLOW, GREEN playing; servants=3, wood=stone=2, coins=5+i
        //BONUS TILE: 1 militaryPoint, 2 coins | 1 wood, 1 stone, 1 servant
        String message = "";
        //first move--------------------------------------------------------------------------------------------------
        boolean exception = false;
        try{
            b.makeMove(mosse.get(0)); //Red fm white slot 18, azione production
        }  catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        Player red = b.getPlayerByColor(Player.PlayerColor.RED);
        //Resources Test
        assertEquals(1, red.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(7, red.getResource(ResourceType.COIN).get());
        //end 1st move-------------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(1)); //Blue fm orange in slotID 18, fallisce per -3 su action value
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        FamilyMember fm = null;
        try {
            fm = b.getCurrentPlayer().getFamilyMemberFromColor("orange");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, fm.isUsed());
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(2)); //Blue fm orange in slotID 18 + 1 servants, legal
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        Player blue = b.getPlayerByColor(Player.PlayerColor.BLUE);
        //Resources Test
        assertEquals(2, blue.getResource(ResourceType.SERVANT).get());
        assertEquals(1, blue.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(8, blue.getResource(ResourceType.COIN).get());
        //end of 2nd move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(3)); //yellow fm orange in slotID 22, 3 mpts 2 coins
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        Player yellow = b.getPlayerByColor(Player.PlayerColor.YELLOW);
        //Resources Test
        assertEquals(9, yellow.getResource(ResourceType.COIN).get());
        assertEquals(3, yellow.getResource(ResourceType.MILITARYPOINTS).get());
        //end of 3rd move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(4)); //Green fm orange in slotID 19, legal
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        Player green = b.getPlayerByColor(Player.PlayerColor.GREEN);
        //Resources Test
        assertEquals(4, green.getResource(ResourceType.SERVANT).get());
        assertEquals(3, green.getResource(ResourceType.STONE).get());
        assertEquals(3, green.getResource(ResourceType.WOOD).get());
        //end of 4th move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(5)); //Red fm orange slot 18 + 1 servant, exception, già fm stesso colore
        }  catch (ActionAbortedException ae){
            exception = true;
            message = ae.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        assertEquals("This Area is already occupied by one of your non-neutral Family Member", message);
        assertEquals(3, red.getResource(ResourceType.SERVANT).get());
        //CHEAT MODE player red---------------------------------------------------------------------------------------
        red.getResource(ResourceType.SERVANT).add(1);
        //re-try------------------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(6)); //Red fm neutral slot 18 + 4 servant, legal
        }  catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        assertEquals(0, red.getResource(ResourceType.SERVANT).get());
        assertEquals(2, red.getResource(ResourceType.MILITARYPOINTS).get());
        assertEquals(9, red.getResource(ResourceType.COIN).get());
        //end of 5th move----------------------------------------------------------------------------------------------
        exception = false;
        int bluecoin = blue.getResource(ResourceType.COIN).get();
        try{
            b.makeMove(mosse.get(7)); //Blue fm white in slotID 23, 2 privileges-> 2 servant, 1 faithpoint
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        assertEquals(4, blue.getResource(ResourceType.SERVANT).get());
        assertEquals(1, blue.getResource(ResourceType.FAITHPOINTS).get());
        assertEquals(bluecoin, blue.getResource(ResourceType.COIN).get());
        //end of 6th move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(8)); //Yellow fm black in slotID 19, legal
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        assertEquals(4, green.getResource(ResourceType.SERVANT).get());
        assertEquals(3, green.getResource(ResourceType.STONE).get());
        assertEquals(3, green.getResource(ResourceType.WOOD).get());
        //end of 7th move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(9)); //Green fm black in slotId 19, fallisce, già fm stesso colore
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(true, exception);
        //cheat mode: --------------------------
        green.getResource(ResourceType.SERVANT).add(3);
        //re-try-------------------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(10)); //Green fm neutral + 4 servant in slotID 19, legal
        }  catch (ActionAbortedException ae){
            exception = true;
            ae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        assertEquals(4, green.getResource(ResourceType.SERVANT).get());
        assertEquals(4, green.getResource(ResourceType.STONE).get());
        assertEquals(4, green.getResource(ResourceType.WOOD).get());
        //end of 8th move----------------------------------------------------------------------------------------------
        int redservant= red.getResource(ResourceType.SERVANT).get();
        exception = false;
        try{
            b.makeMove(mosse.get(11)); //Red fm black in slotID 19, legal
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        redservant+=1;
        assertEquals(redservant, red.getResource(ResourceType.SERVANT).get());
        assertEquals(3, red.getResource(ResourceType.STONE).get());
        assertEquals(3, red.getResource(ResourceType.WOOD).get());
        //end of 9th move----------------------------------------------------------------------------------------------
        exception = false;
        try{
            b.makeMove(mosse.get(12)); //Blue fm black in slotID 19, legal
        }  catch (ActionAbortedException ae){
            exception = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(false, exception);
        //Resources Test
        assertEquals(5, blue.getResource(ResourceType.SERVANT).get());
        assertEquals(3, blue.getResource(ResourceType.STONE).get());
        assertEquals(3, blue.getResource(ResourceType.WOOD).get());
        //end of 10th move---------------------------------------------------------------------------------------------
        /**/
    }
}

