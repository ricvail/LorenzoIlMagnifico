package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ForeachManager;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

public class ForeachImmediate extends AbstractDecorator {

    private counter counter;
    ResourceType resObtained;
    float ratio;

    /**
     *Class constructor. Decorates the immediate "for each" effect, in this case has to count a resource type.
     *
     * @param c card to be decorated
     * @param resObtained resource to be added applying the effect
     * @param ratio ratio between resource to add and value counted
     * @param resCounted resource type to be counted
     */
    public ForeachImmediate(iCard c, ResourceType resObtained, float ratio, ResourceType resCounted) {
        super(c);
        this.counter = p -> ForeachManager.getCount(p, resCounted);
        this.resObtained=resObtained;
        this.ratio=ratio;
    }

    /**
     *Class constructor. Decorates the immediate "for each" effect, in this case has to count the cards of a certain type.
     *
     * @param c card to be decorated
     * @param resObtained resource to be added applying the effect
     * @param ratio ratio between resource to add and value counted
     * @param cardCounted card type to be counted
     */
    public ForeachImmediate(iCard c, ResourceType resObtained, float ratio, Card.CardType cardCounted) {
        super(c);
        this.counter = p -> ForeachManager.getCount(p, cardCounted);
        this.resObtained=resObtained;
        this.ratio=ratio;
    }

    @Override
    public void drawCard(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        ForeachManager.applyForeach(fm.owner, resObtained, ratio, counter.getCount(fm.owner));
        try {
            super.drawCard(move, fm);
        } catch (ActionAbortedException e){
            ForeachManager.applyForeach(fm.owner, resObtained, ratio*-1, counter.getCount(fm.owner));
            throw e;
        }
    }

    @Override
    public void undoDrawCard(JsonNode move, FamilyMember fm) {
        ForeachManager.applyForeach(fm.owner, resObtained, ratio*-1, counter.getCount(fm.owner));
        super.undoDrawCard(move, fm);
    }
}

interface counter {

    /**
     * Takes a player, that is accessed to count a resource or number of cards of a certain type.
     *
     * @param p player to whom apply the effect
     * @return counting result
     */
    int getCount(Player p);
}