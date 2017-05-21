package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by RICVA on 21/05/2017.
 */
public class PrivilegeManager {

    public static void applyDifferentPrivileges(Player p, JsonNode j){
        if (checkDifferentPrivileges(j)){
            int i = 0;
            while (j.has(i)){
                applyPrivilege(p, j.get(i).asInt());
                i++;
            }
        } else {
            //trow exception?
        }
    }

    public static boolean checkDifferentPrivileges(JsonNode j){
        int i =0;
        while (j.has(i)){
            int k =i+1;
            while (j.has(k)) {
                if (j.get(i).asInt() == j.get(k).asInt()){
                    return false;
                }
                k++;
            }
            i++;
        }
        return true;
    }

    private static void applyPrivilege(Player p,int i){
        //TODO
    }

}
