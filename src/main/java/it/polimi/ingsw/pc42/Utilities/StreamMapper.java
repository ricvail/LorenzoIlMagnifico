package it.polimi.ingsw.pc42.Utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by diego on 28/06/2017.
 */
public class StreamMapper {

    static ObjectMapper mapper;

    /**
     * Takes the message string and maps it returning a <code>JsonNode</code> node of the higher level.
     *
     * @param message input message from client
     * @return node of the higher level of the mapped string
     * @throws IOException if happens an issue with the input message and the mapper fails
     */
    public static JsonNode fromStringToJson(String message) throws IOException {

        if (mapper==null) mapper = new ObjectMapper();
        JsonNode node = null;
        node = mapper.readTree(message);
        return node;
    }
}
