package it.polimi.ingsw.pc42.View;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by RICVA on 04/07/2017.
 */
public class MoveBuilder {




    public static ObjectNode createBlankMove(boolean checking){
        ObjectNode o = JsonNodeFactory.instance.objectNode();
        o.put("type", "MOVE");
        o.put("checking", checking);
        return o;
    }
    public static void setChecking(ObjectNode move, boolean checking){
        move.put("checking", checking);
    }

    public static ObjectNode inner(ObjectNode move){
        if (move.has("immediateEffect")){
            return (ObjectNode) move.get("immediateEffect");
        } else
        {
            move.set("immediateEffect",JsonNodeFactory.instance.objectNode());
            return (ObjectNode) move.get("immediateEffect");
        }
    }

    public static void addField(ObjectNode move, ObjectNode serverResponsePayload, String userChoice){
        //Before calling make sure to have a server response with type == WARNING.
        //This function does not expect the whole server response, only the parsed payload
        int level  = serverResponsePayload.get("level").asInt();
        String field = serverResponsePayload.get("field").asText();
        for (int i = 0; i<level; i++){
            move = inner(move);
        }
        if (field.equalsIgnoreCase("familyMember")){
            try {
                int i = Integer.parseInt(userChoice);
                userChoice= serverResponsePayload.get("options").get(i).asText();
            } catch (Exception e) {
            }
            move.put(field, userChoice);
        } else if (field.equalsIgnoreCase("slotID")){
            move.put(field, userChoice);
        } else if (field.equalsIgnoreCase("servants")){
            if (userChoice.equalsIgnoreCase("a")){
                userChoice= serverResponsePayload.get("options").get(0).asText();
            }
            try {
                move.put(field, Integer.parseInt(userChoice));
            } catch (Exception e){
                move.put(field, (userChoice));
            }
        }else if (field.equalsIgnoreCase("privileges")){
            try {
                if (move.has(field)){
                    ((ArrayNode)move.get(field)).add(Integer.parseInt(userChoice));
                } else {
                    ArrayNode node = JsonNodeFactory.instance.arrayNode();
                    node.add(Integer.parseInt(userChoice));
                    move.set(field, node);
                }
            }catch (Exception e){

            }
        }else if (field.equalsIgnoreCase("paymentChoice")){
            try {
                move.put(field, Integer.parseInt(userChoice));
            } catch (Exception e){

            }
        }else if (field.equalsIgnoreCase("vaticanChoice")){
            if (userChoice.equalsIgnoreCase("y")){
                move.put(field, true);
            }else if (userChoice.equalsIgnoreCase("n")){
                move.put(field, false);
            }
        }
    }
    
}
