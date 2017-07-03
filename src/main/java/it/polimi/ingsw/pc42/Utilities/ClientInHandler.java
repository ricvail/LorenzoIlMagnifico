package it.polimi.ingsw.pc42.Utilities;

import java.util.Scanner;

/**
 * Created by diego on 28/06/2017.
 */
public class ClientInHandler implements Runnable {
    private Scanner socketIn;

    public ClientInHandler(Scanner socketIn) {
        this.socketIn=socketIn;
    }

    public void run() {
        boolean end= false;
        while (!end) {
            String line = socketIn.nextLine();
            System.out.println(line);
        }
    }
}

