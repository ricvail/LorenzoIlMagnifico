package it.polimi.ingsw.pc42.Control;

import it.polimi.ingsw.pc42.Control.DevelopmentCards.Card;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.Player;

/**
 * Created by RICVA on 16/06/2017.
 */
public class ForeachManager {

    public static int getCount (Player p, Card.CardType card){
        return p.getNumberOfCards(card);
    }

    public static int getCount (Player p, ResourceType res){
        return p.getResource(res).get();
    }

    public static void applyForeach (Player p, ResourceType resObtained, float ratio, int counted){
        p.getResource(resObtained).add((int) (counted*ratio));
    }
}
