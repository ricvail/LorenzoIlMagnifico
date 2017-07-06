package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ForeachManager;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

/**
 * Created by RICVA on 16/06/2017.
 */
public class ForeachImmediate extends AbstractDecorator {

    private counter counter;
    ResourceType resObtained;
    float ratio;

    public ForeachImmediate(iCard c, ResourceType resObtained, float ratio, ResourceType resCounted) {
        super(c);
        this.counter =new counter(){
            @Override
            public int getCount(Player p) {
                return ForeachManager.getCount(p, resCounted);
            }
        };
        this.resObtained=resObtained;
        this.ratio=ratio;
    }
    public ForeachImmediate(iCard c, ResourceType resObtained, float ratio, Card.CardType cardCounted) {
        super(c);
        this.counter =new counter(){
            @Override
            public int getCount(Player p) {
                return ForeachManager.getCount(p, cardCounted);
            }
        };
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
    int getCount(Player p);
}