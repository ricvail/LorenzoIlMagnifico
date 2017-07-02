package it.polimi.ingsw.pc42.Model;


import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;

import java.util.ArrayList;

public class PersonalBonusTile {

    public PersonalBonusTile() {
        this.harvestBonuses = new ArrayList<ResourceWrapper>();
        this.productionBonuses = new ArrayList<ResourceWrapper>();
    }

    public ArrayList<ResourceWrapper> harvestBonuses, productionBonuses;

    public static void applyBonuses(ArrayList<ResourceWrapper> bonuses, Player p){
        for (ResourceWrapper bonus : bonuses){
            p.getResource(bonus.getResourceType()).add(bonus.get());
        }
    }
    public static void undoBonuses(ArrayList<ResourceWrapper> bonuses, Player p){
        for (ResourceWrapper bonus : bonuses){
            p.getResource(bonus.getResourceType()).add(bonus.get()*-1);
        }
    }
}
