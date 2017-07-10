# Lorenzo il Magnifico - GC42

## Running the game
To start the server launch the main method found in \src\main\java\it\polimi\ingsw\pc42\Utilities\Server.java.<br>
To start each of the clients launch the main method found in \src\main\java\it\polimi\ingsw\pc42\View\Client.java <br>
Do not start the server more than once.

## Game Configuration
The configuration files can be found in \src\res\. There are 6 .json files that allow you to change various aspects of 
the game, such as adding or modifying cards, action spaces and privileges. The user can further customize the game using
personalized bonus tiles, changing the victory point bonuses associated with the faith point track, or setting a
different timeout for user moves.

## Implemented features
- Basic rules
- Socket connection
- Command Line Interface
- The server can support multiple games
- If a client crashes the user can start a new instance and re-join the game
- Cards permanent effects
## Class structure overview

###Server
All card and actionSpace effects are implemented as decorators of their respective base class. <br>
We exploited the mechanism of catching and rethrowing Exceptions to bubble up through the stack of method calls, in case
something goes wrong when applying a particular decorator (for example is the user is left with negative resources 
because he tried a move he could not afford)<br>
Board holds a list of action spaces and of players (for turn management). This class also holds a reference to the Dices. <br>
Game Initializer has the job of instantiating the board. It reads configuration data from the .json files mentioned above
and also offers the possibility to instantiate a non-randomized version of the board, for testing purposes.<br>
The Player class contains a list of ResourceWrappers and of Cards.
Turn management, Vatican phase, and end-of-round logic are all handled automatically by the Board (or delegated to the
action spaces).<br>

###Communication
Messages exchanged between clients and the server are all in Json format. <br>
To perform a move, the user creates a blank json object and sends it to the server, which in turn replies with 
another json containing information about the next field to be completed. The exchange continues until the server
replies with a message stating that the move is complete, and in then the user has the possibility to perform it<br>
The server can also send on request a Json containing information about the board status, which is parsed by the client
and displayed to the user in a readable format.

###Client
Most of the client side logic is used to manipulate the json of the move being built by the user, according to his
commands and the server responses.<br>
OutputStringGenerator contains the methods that parse the json representation of the board and print it to the user.