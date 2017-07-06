package it.polimi.ingsw.pc42.Control;

public class IntWrapper implements iIntWrapper{

    protected int myInt;

    /**
     * Class constructor. Sets the attribute for the quantity equal to the parameter passed.
     * @param i quantity to be set
     */
    public IntWrapper(int i){
        myInt=i;
    }

    /**
     * Class constructor. Sets the attribute for the quantity to zero.
     */
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

}
