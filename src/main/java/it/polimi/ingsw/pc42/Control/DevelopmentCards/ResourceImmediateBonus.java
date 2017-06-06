package it.polimi.ingsw.pc42.Control.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Model.Player;
import it.polimi.ingsw.pc42.Control.ResourceType;

public class ResourceImmediateBonus extends AbstractDecorator {
    private ResourceType resourceType;
    private int q;

    public ResourceImmediateBonus(ResourceType rt, int quantity, iCard c) {
        super(c);
        q= quantity;
        resourceType=rt;
    }

    @Override
    public void applyDrawEffect(Player player, JsonNode json) {
        player.getResource(resourceType).add(q);
        super.applyDrawEffect(player, json);
    }

    @Override
    public boolean drawRequirementCheck (Player player){
        try{
            player.getResource(resourceType).add(q);
        }
        catch (IllegalArgumentException e){
            player.getResource(resourceType).add(q*-1);
            return false;
        }
        boolean b=super.drawRequirementCheck(player);
        player.getResource(resourceType).add(q*-1);
        return b;
    }

}
