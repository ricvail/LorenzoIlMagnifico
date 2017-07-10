package it.polimi.ingsw.pc42.Control.ActionSpace;


public enum Area {

    TERRITORY("territory"), BUILDING("building"), CHARACTER("character"), VENTURE("venture"),
    COUNCIL("council"), MARKET("market"), HARVEST("harvest"), PRODUCTION("production"), NULL("null");

    private String area;

    public String getAreaString(){
        return area;
    }

    /**
     Enum constructor. Set the string attribute according to the parameter.
     * @param area
     */
    Area(String area) {
        this.area = area;
    }

    /**
     * Returns an area, if it finds a match for the string passed as parameter,
     * iterating over the Enum values.
     *
     * @param a area string that need the check
     * @return an area, if matches the parameter
     */
    public static Area fromString(String a){
        for (Area area : Area.values()) {
            if (area.getAreaString().equalsIgnoreCase(a)) {
                return area;
            }
        }
        throw new IllegalArgumentException();
    }
}
