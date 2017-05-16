package it.polimi.ingsw.pc42;


import it.polimi.ingsw.pc42.ActionSpace.AbstractActionSpace;

import java.util.ArrayList;

public interface Board {

    ArrayList<AbstractActionSpace> actionSpaces;
    ArrayList<Player> turnOrder;

}