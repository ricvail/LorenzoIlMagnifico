package it.polimi.ingsw.pc42.Model;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class PersonalBonusTile {

    public ArrayList<ResourceWrapper> harvestBonuses, productionBonuses;
    public JsonNode json;
    private static Logger logger=LogManager.getLogger();

    /**
     * Class constructor. Initializes the harvest and production lists.
     */
    public PersonalBonusTile(JsonNode json) {
        this.harvestBonuses = new ArrayList<>();
        this.productionBonuses = new ArrayList<>();
        this.json=json;
    }

    /**
     * Iterates through the list of bonuses that take as a parameter and add each value to the correct resource of the
     * player passed.
     *
     * @param bonuses list of wrapped bonuses, representing the bonus tile
     * @param p player owner of the tile
     */
    public static void applyBonuses(ArrayList<ResourceWrapper> bonuses, Player p){
        for (ResourceWrapper bonus : bonuses){
            p.getResource(bonus.getResourceType()).add(bonus.get());
        }
    }

    /**
     *Iterates through the list of bonuses that take as a parameter and subtract each value from the correct resource
     *  of the player passed.
     *
     * @param bonuses list of wrapped bonuses, representing the bonus tile
     * @param p player owner of the tile
     */
    public static void undoBonuses(ArrayList<ResourceWrapper> bonuses, Player p){
        for (ResourceWrapper bonus : bonuses){
            try {
                p.getResource(bonus.getResourceType()).add(bonus.get()*-1);
            } catch (Exception e){
                logger.error(e);
            }
        }
    }
}
