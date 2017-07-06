package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.PrintWriter;

/**
 * Created by RICVA on 05/07/2017.
 */
public abstract class MessageSender {

    public PrintWriter socketOut;

    public void sendMessage(String type, JsonNode payload){
        JsonNodeFactory factory= JsonNodeFactory.instance;
        ObjectNode message = factory.objectNode();
        message.put("type", type);
        message.set("payload", payload);
        socketOut.println(message.toString());
        socketOut.flush();
    }

}
