package it.polimi.ingsw.pc42.Control;

public enum ResourceType{

    STONE("stone"), WOOD("wood"), SERVANT("servants"), COIN("coins"), FAITHPOINTS("faithPoints"),
    VICTORYPOINTS("victoryPoints"), MILITARYPOINTS("militaryPoints");

    private String resourceType;

    public String getString(){
        return resourceType;
    }

    /**
     * Enum constructor. Set the string attribute according to the parameter.
     *
     * @param resourceType resource type string that needs the "check"
     */
    ResourceType(String resourceType){
        this.resourceType = resourceType;
    }

    /**
     * Returns a resource type, if it finds a match for the string passed as parameter,
     * iterating over the Enum values.
     *
     * @param rt resource type string that needs the "check"
     * @return a resource type value, if matches the parameter
     */
    public static ResourceType fromString(String rt) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getString().equalsIgnoreCase(rt)) {
                return resourceType;
            }
        }
        throw new IllegalArgumentException();
    }

}
