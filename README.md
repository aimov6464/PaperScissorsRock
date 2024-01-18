Hi all!
This is a simple multiplayer Rock, Paper, Scissors game implemented in Java using ServerSocker, Socket.
To run it you need to run Server.java
Next, two participants can connect using telnet, port 12345 and enter their name.
The rules of the game are very simple:
  1) You need to enter R (Rock), P (Paper) or S (Scissors) into the command line, and wait until your opponent does the same.
  2) The server will determine the winner and close the session, if you have a draw, then you will continue to play.
