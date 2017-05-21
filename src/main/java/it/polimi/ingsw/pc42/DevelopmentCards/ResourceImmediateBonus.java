package it.polimi.ingsw.pc42.DevelopmentCards;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Player;
import it.polimi.ingsw.pc42.ResourceType;

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

}
