package it.polimi.ingsw.pc42.Utilities;

import it.polimi.ingsw.pc42.Model.Board;

/**
 * Wrapper around a board object
 */
public class BoardProvider {

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board board;

}
