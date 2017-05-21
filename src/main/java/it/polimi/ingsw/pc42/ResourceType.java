package it.polimi.ingsw.pc42;

public enum ResourceType{
    STONE("stone"), WOOD("wood"), SERVANT("servant"), COIN("coin"), FAITH("faith"), VICTORY("faith"), MILITARY("military");

    private String resourceType;
    ResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    public String getRTString(){
        return resourceType;
    }


}
