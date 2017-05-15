package it.polimi.ingsw.pc42.Utilities;

public interface iIntWrapper
{
    int get();
    void set(int i) throws IllegalArgumentException;
    void add(int i) throws IllegalArgumentException;
}
