package it.polimi.ingsw.pc42.View;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by diego on 28/06/2017.
 */
public class ClientOutHandler implements Runnable {

    private PrintWriter socketOut;

    public ClientOutHandler(PrintWriter socketOut) {
        this.socketOut = socketOut;
    }

    public void run() {
        boolean end=false;
        Scanner stdin = new Scanner(System.in);
        while (!end) {
            String inputLine = stdin.nextLine();
            socketOut.println(inputLine);
            socketOut.flush();
        }

    }
}
