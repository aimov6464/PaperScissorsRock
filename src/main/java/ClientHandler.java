import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private AtomicBoolean isGameInProgress;
    private AtomicBoolean isDraw;
    private static List<Map<String, String>> choiceList = new ArrayList<>(2);

    public ClientHandler(Socket clientSocket, AtomicBoolean isGameInProgress, AtomicBoolean isDraw) {
        this.clientSocket = clientSocket;
        this.isGameInProgress = isGameInProgress;
        this.isDraw = isDraw;
    }

    @Override
    public void run() {
        try {
            isGameInProgress.set(true);
            isDraw.set(false);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();
            String name;

            name = getName(outputStream, reader);
            Map<String, String> map = getChoiceMap(outputStream, reader, name);
            createChoiceList(map);

            map = gameProcess(map, outputStream, reader, name);
            choiceList.clear();
            map.clear();
            if (!isDraw.get()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> gameProcess(Map<String, String> map, OutputStream outputStream,
                                            BufferedReader reader, String name) throws IOException {
        while (isGameInProgress.get()) {
            if (choiceList.size() > 1){
                Object choice1 = choiceList.get(0).values().toArray()[0];
                Object choice2 = choiceList.get(1).values().toArray()[0];

                Object player1 = choiceList.get(0).keySet().toArray()[0];
                Object player2 = choiceList.get(1).keySet().toArray()[0];

                System.out.println(choice1.toString() + player1);
                System.out.println(choice2.toString() + player2);

                String p1W = player1 + " Wins.";
                String p2W = player2 + " Wins.";
                String draw = "Its a draw." + System.lineSeparator();

                map = determineWinner(map, outputStream, reader, name, choice1, choice2, draw, p1W, p2W);
            }
        }
        return map;
    }

    private Map<String, String> determineWinner(Map<String, String> map, OutputStream outputStream,
                                                BufferedReader reader, String name, Object choice1,
                                                Object choice2, String draw, String p1W, String p2W) throws IOException {
        if (choice1.equals(choice2)) {
            choiceList.clear();
            map.clear();
            outputStream.write(draw.getBytes());
            map = getChoiceMap(outputStream, reader, name);
            createChoiceList(map);
            isDraw.set(true);
        } else if (choice1.equals("R") && choice2.equals("S")) {
            outputStream.write(p1W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        } else if (choice1.equals("S") && choice2.equals("R")) {
            outputStream.write(p2W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        } else if (choice1.equals("R") && choice2.equals("P")) {
            outputStream.write(p2W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        } else if (choice1.equals("P") && choice2.equals("R")) {
            outputStream.write(p1W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        } else if (choice1.equals("S") && choice2.equals("P")) {
            outputStream.write(p1W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        } else if (choice1.equals("P") && choice2.equals("S")) {
            outputStream.write(p2W.getBytes());
            isGameInProgress.set(false);
            isDraw.set(false);
        }
        return map;
    }

    private static String getName(OutputStream outputStream, BufferedReader reader) throws IOException {
        String name;
        String askName = "Enter your name: ";
        outputStream.write(askName.getBytes());
        name = reader.readLine();
        return name;
    }

    private static void createChoiceList(Map<String, String> map) {
        synchronized (choiceList) {
            choiceList.add(map);
        }
    }

    private static Map<String, String> getChoiceMap(OutputStream outputStream,
                                                    BufferedReader reader, String name) throws IOException {
        String choice;
        String askChoice = "Start the game by selecting (R)ock (P)aper, (S)cissors: ";
        outputStream.write(askChoice.getBytes());
        choice = reader.readLine();
        Map<String, String> map = new HashMap<>();
        synchronized (map) {
            map.put(name, choice);
        }
        return map;
    }
}