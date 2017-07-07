package it.polimi.ingsw.pc42.Control;

import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Model.Player;

public class ForeachManager {

    /**
     * Takes a player and returns the number of cards of particular type owned.
     *
     * @param p player to be analyzed
     * @param card card type to be counted
     * @return number of cards of a specific type owned by the player
     */
    public static int getCount (Player p, Card.CardType card){
        return p.getNumberOfCards(card);
    }

    /**
     * Takes a player and returns the amount of resource of particular type owned.
     *
     * @param p player to be analyzed
     * @param res resource type to be counted
     * @return  resource amount, of a specific type, owned by the player
     */
    public static int getCount (Player p, ResourceType res){
        return p.getResource(res).get();
    }

    /**
     * Applies the "for each" effect: takes the resource to be added, the value counted that determines the amount of
     * the first one together with ratio between the two.
     *
     * @param p player to whom apply the bonus
     * @param resObtained resource to be added applying the effect
     * @param ratio ratio between resource to add and value counted
     * @param counted counter of a previously specified resource or card type owned
     */
    public static void applyForeach (Player p, ResourceType resObtained, float ratio, int counted){
        p.getResource(resObtained).add((int) (counted*ratio));
    }
}
