package it.polimi.ingsw.pc42;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

/**
 * Created by RICVA on 21/05/2017.
 */
public class PrivilegeManager {



    static JsonNode getPrivileges(){
        return null;
    }

    public static void applyDifferentPrivileges(Player p, JsonNode j){
        if (checkDifferentPrivileges(j)){
            int i = 0;
            while (j.has(i)){
                applyPrivilege(p, j.get(i).asInt(), getPrivileges());
                i++;
            }
        } else {
            //trow exception?
        }
    }

    public static void undoPrivileges(Player p, JsonNode j){
        int i=0;
        while (j.has(i)){
            undoPrivilege(p, j.get(i).asInt(), getPrivileges());
            i++;
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

    public static void applyPrivilege(Player p,int i, JsonNode privileges){
        usePrivilege(p, i, privileges, false);
    }
    public static void undoPrivilege(Player p,int i, JsonNode privileges){
        usePrivilege(p, i, privileges, true);
    }

    private static void usePrivilege(Player p,int i, JsonNode privileges, boolean subtract){
        if (privileges.has(i)){
            Iterator<String> iterator = privileges.get(i).fieldNames();
            while (iterator.hasNext()){
                String key = iterator.next();
                ResourceType type = ResourceType.fromString(key);
                int q = privileges.get(i).get(key).asInt();
                if (subtract){
                    p.getResource(type).add(q*-1);
                } else{
                    p.getResource(type).add(q);
                }
            }
        } else{
            //throw exception "privilege non existent"
        }
    }

}
