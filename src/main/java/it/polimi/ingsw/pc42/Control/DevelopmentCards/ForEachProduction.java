package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ActionAbortedException;
import it.polimi.ingsw.pc42.Control.ForeachManager;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Model.FamilyMember;
import it.polimi.ingsw.pc42.Model.Player;

/**
 * Created by diego on 08/07/2017.
 */
public class ForEachProduction extends AbstractDecorator {
    private counter counter;
    ResourceType resObtained;
    float ratio;

    public ForEachProduction(iCard c, ResourceType resObtained, float ratio, ResourceType resCounted) {
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

    @Override
    public void onProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            ForeachManager.applyForeach(fm.owner, resObtained, ratio, counter.getCount(fm.owner));
            super.onProduction(move, fm);
        }
    }

    @Override
    public void undoOnProduction(JsonNode move, FamilyMember fm) throws ActionAbortedException {
        if (fm.getValue()>=getActionValue()) {
            ForeachManager.applyForeach(fm.owner, resObtained, ratio * -1, counter.getCount(fm.owner));
            super.undoOnProduction(move, fm);
        }
    }
}
