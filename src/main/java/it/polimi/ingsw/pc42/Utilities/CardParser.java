package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.polimi.ingsw.pc42.Control.ActionSpace.Area;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.*;
import it.polimi.ingsw.pc42.Control.DevelopmentCards.Permanent.*;
import it.polimi.ingsw.pc42.Control.ResourceType;
import it.polimi.ingsw.pc42.Control.ResourceWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class CardParser {
    private static Logger logger= LogManager.getLogger();

    public static iCard createCard(JsonNode root, BoardProvider bp){
        return createCard(root, bp, false);
    }
    /**
     * Acts as factory method for the initialization of a card and then delegates the decoration of costs and
     * effects.
     *
     * @param root is the highest node of a single card object in the JSON file
     * @param bp is a wrapper for a board object
     * @return an object of card interface, already decorated
     */
    public static iCard createCard(JsonNode root, BoardProvider bp, boolean advanced){

        iCard c = null;
        try {
            isJsonValid(root);

            Card card;

            card = new Card(root.get("era").asInt(),
                    root.get("name").asText(),
                    Card.CardType.fromString(root.get("type").asText()),
                    root, bp);

            if (root.has("activationCost")){
                card.setActionValue(root.get("activationCost").asInt());
            }
            c=card;

            //decorate immediate effect
            if (root.has("immediateEffect")) {
                JsonNode immediateEffectNode = root.get("immediateEffect");
                c = immediateEffectIterator(immediateEffectNode, c);
            }

            if (root.has("permanentEffects")&&advanced) {
                JsonNode permanentEffectNode = root.get("permanentEffects");
                if (permanentEffectNode.size()>1){
                    productionChoice choiceDec = new productionChoice(c);
                    for (int i = 0; i<permanentEffectNode.size(); i++){
                        choiceDec.addChoice();
                        choiceDec.choices.set(i, permanentEffectIterator(permanentEffectNode.get(i),
                                choiceDec.choices.get(i)));
                    }
                    c=choiceDec;
                } else {
                    c = permanentEffectIterator(permanentEffectNode.get(0), c);
                }
            }

            //decorate costs
            if (root.has("costs")) {
                ArrayNode costsNode = (ArrayNode) root.get("costs");
                c = costIterator(costsNode, c);
            }

        } catch (Exception e){
            System.out.println(root.get("name").asText());
            logger.info(e);
        }
        return c;
    }

    private static iCard permanentEffectIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            if (c.getCardType()== Card.CardType.TERRITORY){
                try {
                    c = new harvestResource(ResourceType.fromString(key), jsonNode.get(key).asInt(), c);
                } catch (IllegalArgumentException e){
                    logger.info(e);
                    if ("privileges".equalsIgnoreCase(key)){
                        c= new harvestPrivileges(jsonNode.get(key).asInt(), c);
                    }else{
                        throw new Exception("Invalid territory field: "+ key);
                    }
                }
            }else if (c.getCardType()== Card.CardType.BUILDING) {
                try {
                    c = new productionResource(ResourceType.fromString(key), jsonNode.get(key).asInt(), c);
                } catch (IllegalArgumentException e){
                    logger.info(e);
                    if ("privileges".equalsIgnoreCase(key)){
                        c= new productionPrivileges(jsonNode.get(key).asInt(), c);
                    } else if ("foreach".equalsIgnoreCase(key)){
                        c=applyForeachProduction(jsonNode.get(key), c);
                    } else{
                        throw new Exception("Invalid building field: "+ key);
                    }
                }
            }
            if ("addValueToDice".equalsIgnoreCase(key)){
                JsonNode addValue = jsonNode.get(key);
                ArrayList<ResourceWrapper> bonusList= new ArrayList<>();
                if (addValue.has("value")&&addValue.has("type")){
                    Iterator<String> bonuses = addValue.fieldNames();
                    while (bonuses.hasNext()){
                        String bonus= bonuses.next();
                        try {
                            bonusList.add(new ResourceWrapper(
                                    ResourceType.fromString(bonus), addValue.get(bonus).asInt()
                            ));
                        } catch (IllegalArgumentException e){
                            logger.info(e);
                            if ("value".equalsIgnoreCase(bonus)||"type".equalsIgnoreCase(bonus));
                            else throw new Exception("Missing value or type on AddValueToDice");
                        }
                    }
                    c= new DiceBonus(bonusList, addValue.get("value").asInt(), Area.fromString(addValue.get("type").asText()), c);
                } else {
                    throw new Exception("Missing value or type on AddValueToDice");
                }
            }
            if ("disableImmediateBonus".equalsIgnoreCase(key)&&jsonNode.get(key).asBoolean()){
                c= new DisableImmediateBonus(c);
            }
            if ("finalVictoryPoint".equalsIgnoreCase(key)){
                if (jsonNode.get(key).isInt()){
                    c = new endGameVictoryPoints(jsonNode.get(key).asInt(), c);
                } else{
                    throw new Exception("Invalid finalVictoryPoint field");
                }
            }
        }
        return c;
    }

    /**
     *Takes the base card and decorates around it the immediate effect, throws exception if one of the effect's
     * specification in the JSON file is not valid.
     *
     * @param jsonNode   is the immediate effect node of a card object in the JSON file
     * @param c is the basic card, initialized in the caller
     * @return an object that implements the card interface, decorated with the immediate effect
     * @throws Exception if a field has an invalid specification of part of the effect to be decorated
     */
    private static iCard immediateEffectIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                c = applyResource(key, jsonNode.get(key).asInt(), c);
            } catch (IllegalArgumentException e){
                if ("privileges".equalsIgnoreCase(key)){
                    if (jsonNode.get(key).isInt()){
                        c = new PrivilegeImmediateBonus(jsonNode.get(key).asInt(), c);
                    } else{
                        throw new Exception("Invalid privileges field");
                    }
                } else if ("card".equalsIgnoreCase(key)){
                    c = applyExtraCardBonus(c, jsonNode.get("card"));

                } else if ("foreach".equalsIgnoreCase(key)){
                    c = applyForeachImmediate(jsonNode.get("foreach"), c);
                } else {
                    throw new Exception("Invalid immediate effect: "+ key);
                }
                logger.info(e);
            }
        }
        return c;
    }

    /**
     * Initializes the resource type based on the parameter passed and delegates the decoration of the resource
     * immediate bonus (both effect and cost) around the card.
     *
     * @param s resource to be decorated
     * @param q quantity of the resource
     * @param c an object that implements the card interface, already initialized
     * @return an object that implements the card interface, decorated with a resource immediate bonus
     */
    private static iCard applyResource(String s, int q, iCard c){
        ResourceType rt = ResourceType.fromString(s);
        c = new ResourceImmediateBonus(rt, q, c);
        return c;

    }

    /**
     * Checks the <code>JsonNode</code> of the immediate effect for the extra card bonus to be decorated, delegating
     * the actual decoration. Throws exception if a needed field is missing or the correspondent specification is
     * not valid.
     *
     * @param c an object that implements the card interface, already initialized
     * @param node the extra card node of a card in the JSON file
     * @return an object that implements the card interface, decorated with a extra card bonus
     * @throws Exception if a needed field is missing or is not a valid card type
     */
    private static iCard applyExtraCardBonus(iCard c, JsonNode node) throws Exception {
        if (!(node.has("value")&&node.has("type"))){
            throw new Exception("missing value or type");
        }
        ArrayList<Area> areas = new ArrayList<>();
        ArrayList<ExtraCard.bonus> bonuses = new ArrayList<>();
        int value = 0;
        Iterator<String> keys = node.fieldNames();
        while ((keys.hasNext())){
            String key = keys.next();
            try {
                ResourceType rt = ResourceType.fromString(key);
                bonuses.add(new ExtraCard.bonus(rt, node.get(key).asInt()));
            } catch (Exception e){
                logger.info(e);
                if ("value".equalsIgnoreCase(key)){
                    value=node.get(key).asInt();
                } else if ("type".equalsIgnoreCase(key)){
                    String type = node.get(key).asText();
                    try {
                        Area a = Area.fromString(type);
                        areas.add(a);
                    } catch (Exception ex) {
                        logger.info(ex);
                        if ("all".equalsIgnoreCase(type)) {
                            areas.add(Area.TERRITORY);
                            areas.add(Area.BUILDING);
                            areas.add(Area.CHARACTER);
                            areas.add(Area.VENTURE);
                            logger.info(e);
                        } else {
                            throw new Exception("Not a valid card type: " + type);
                        }
                    }
                }
            }
        }
        return new ExtraCard(c, areas, bonuses, value);
    }

    /**
     *Checks the <code>JsonNode</code> of the immediate effect for the "for each" bonus to be decorated( ex.: 1 victory
     *  points every 2 military points), delegating the actual decoration. It takes, from file, a resource to be add,
     *  a resource or a card type to be counted and ratio for the two values. Re-throws the exception from the for each
     *  decorator if even the card type is not correct.
     *
     * @param jsonNode the "for each" node of a card in the JSON file
     * @param c an object that implements the card interface, already initialized
     * @return an object that implements the card interface, decorated with a "for each" bonus
     * @throws Exception  from the for each decorator if even the card type is not correct
     */
    private static iCard applyForeachImmediate(JsonNode jsonNode, iCard c) throws Exception{
        ResourceType obtained = ResourceType.fromString(jsonNode.get("left").asText());
        try {
            ResourceType toBeCounted = ResourceType.fromString(jsonNode.get("right").asText());
            return new ForeachImmediate(c, obtained,(float) jsonNode.get("ratio").asDouble(), toBeCounted);
        } catch (Exception e){
            logger.info(e);
            Card.CardType toBeCounted = Card.CardType.fromString(jsonNode.get("right").asText());
            return new ForeachImmediate(c, obtained,(float) jsonNode.get("ratio").asDouble(), toBeCounted);
        }
    }
    private static iCard applyForeachProduction(JsonNode jsonNode, iCard c) throws Exception{
        ResourceType obtained = ResourceType.fromString(jsonNode.get("left").asText());
        try {
            ResourceType toBeCounted = ResourceType.fromString(jsonNode.get("right").asText());
            return new ForeachProduction(c, obtained,(float) jsonNode.get("ratio").asDouble(), toBeCounted);
        } catch (Exception e){
            logger.info(e);
            Card.CardType toBeCounted = Card.CardType.fromString(jsonNode.get("right").asText());
            return new ForeachProduction(c, obtained,(float) jsonNode.get("ratio").asDouble(), toBeCounted);
        }
    }

    /**
     * Takes the card already decorated with immediate effect and decorates around it the cost, checking if is single
     * cost or a choice cost in the cost node. Re-throws exception from the decorator.
     *
     * @param jsonNode is the cost node of a card object in the JSON file
     * @param c an object that implements the card interface, already decorated with the immediate effect
     * @return an object that implements the card interface, decorated with the cost bonus
     * @throws Exception from the decorator
     */
    private static iCard costIterator(JsonNode jsonNode, iCard c) throws Exception {
        if (jsonNode.size()>1){
            c = choiceIterator(jsonNode, c);
        } else {
            c = singleCostIterator(jsonNode.get(0), c);
        }
        return c;
    }

    /**
     * Checks the <code>JsonNode</code> of the card cost for the cost bonus to be decorated, delegating
     * the actual decoration, it could be a simple resource cost or a military points cost, with required and
     * subtracted values, in this case it delegates another decorator. Throws exception if the cost specification
     * in the JSON file is not valid.
     *
     * @param jsonNode is the cost node of a card object in the JSON file
     * @param c an object that implements the card interface, already decorated with the immediate effect
     * @return an object that implements the card interface, decorated with the single cost bonus
     * @throws Exception if encounter an invalid military cost specification, or resource cost in general
     */
    private static iCard singleCostIterator(JsonNode jsonNode, iCard c) throws Exception {
        Iterator<String> it = jsonNode.fieldNames();
        while (it.hasNext()) {
            String key = it.next();
            try {
                c = applyResource(key, jsonNode.get(key).asInt()*-1, c);
            } catch (IllegalArgumentException e){
                logger.info(e);
                if ("militaryPointsRequired".equalsIgnoreCase(key)){
                    if (jsonNode.get(key).isInt()&&
                            jsonNode.has("militaryPointsSubtracted")&&
                            jsonNode.get("militaryPointsSubtracted").isInt()){
                        c=new militaryCost(c, jsonNode.get("militaryPointsRequired").asInt(),
                                jsonNode.get("militaryPointsSubtracted").asInt());
                    } else {
                        throw new Exception("Invalid military cost");
                    }
                } else if ("militaryPointsSubtracted".equalsIgnoreCase(key)){
                    //ignore, already added above
                } else {
                    throw new Exception("Invalid cost: "+ key);
                }
            }
        }
        return c;
    }


    /**
     * Checks the <code>JsonNode</code> of the card cost for the choice cost bonus to be decorated, delegating
     * the actual decoration. It creates a set of single cost bonuses. Re-throws the <code>singleCostIterator</code>
     * exception.
     *
     * @param jsonNode is the cost node of a card object in the JSON file
     * @param c an object that implements the card interface, already decorated with the immediate effect
     * @return an object that implements the card interface, decorated with the choice cost bonus
     * @throws Exception
     */
    private static iCard choiceIterator(JsonNode jsonNode, iCard c) throws Exception {
        ImmediateBonusChoice choiceDec = new ImmediateBonusChoice(c);
        for (int i = 0; i<jsonNode.size(); i++){
            choiceDec.addChoice();
            choiceDec.choices.set(i, singleCostIterator(jsonNode.get(i), choiceDec.choices.get(i)));
        }
        return choiceDec;
    }

    /**
     *Iterates through the nodes of <code>JsonNode</code> the card object and checks if all the basic fields
     * for each type of card are present, else throws an exception.
     *
     * @param root is the highest node of a single card object in the JSON file
     * @throws Exception if a basic field is missing in the card
     */
    private static void isJsonValid(JsonNode root) throws Exception{
        if (!(root.has("era")&&root.get("era").isInt()&&
                root.has("name")&&
                root.has("type")
                //&& root.has("id")&&root.get("id").isInt()
        )){
            throw new Exception("missing base field");
        }

        Card.CardType type = Card.CardType.fromString(root.get("type").asText());
        if (type== Card.CardType.TERRITORY || type== Card.CardType.BUILDING){
            if (!(root.has("activationCost")&&root.get("activationCost").isInt())){
                throw new Exception("missing activation cost");
            }
        }

        if (root.has("immediateEffect")&&
                !root.get("immediateEffect").isObject()) {
            throw new Exception("wrong type of immediateEffect");
        }

        if (root.has("permanentEffects")&&
                !root.get("permanentEffects").isArray()) {
            throw new Exception("wrong type of permanentEffects");
        }

        if (root.has("costs")&&
                !root.get("costs").isArray()) {
            throw new Exception("wrong type of costs");
        }
    }
}

