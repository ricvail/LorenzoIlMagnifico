package it.polimi.ingsw.pc42;


import it.polimi.ingsw.pc42.ActionSpace.iActionSpace;

import java.util.ArrayList;

public interface Board {

    ArrayList<iActionSpace> getActionSpaces();

    //ArrayList<Player> turnOrder;

}