package it.polimi.ingsw.pc42.DevelopmentCards;

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
    public void applyDrawEffect(Player player) {
        player.getResource(resourceType).add(q);
        super.applyDrawEffect(player);
    }

}
