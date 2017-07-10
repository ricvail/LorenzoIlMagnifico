package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import it.polimi.ingsw.pc42.Model.PersonalBonusTile;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by RICVA on 01/07/2017.
 */
public class PersonalBonusTileParser {

    public static PersonalBonusTile parse(JsonNode json){
        PersonalBonusTile tile=new PersonalBonusTile(json);

        addBonuses(json.get("harvest"), tile.harvestBonuses);
        addBonuses(json.get("production"), tile.productionBonuses);

        return tile;
    }

    private static void addBonuses(JsonNode json, ArrayList<ResourceWrapper> array){
        Iterator<String> iterator = json.fieldNames();
        while (iterator.hasNext()){
            String key= iterator.next();
            array.add(new ResourceWrapper(ResourceType.fromString(key), json.get(key).asInt()));
        }
    }
}
