package it.polimi.ingsw.pc42.Utilities;

import it.polimi.ingsw.pc42.Player;

public class IntWrapper implements iIntWrapper{

    private int myInt;

    public IntWrapper(int i){
        myInt=i;
    }

    public IntWrapper(){
        myInt=0;
    }


    @Override
    public int get() {
        return myInt;
    }

    @Override
    public void set(int i) {
        if (i<0) {
            throw new IllegalArgumentException();
        }
        myInt=i;
    }

    @Override
    public void add(int i) throws IllegalArgumentException {
        if (myInt+i<0) {
            throw new IllegalArgumentException();
        }
        myInt+=i;
    }
}
