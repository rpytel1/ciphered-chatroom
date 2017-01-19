
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Receiver {

    Database db;
    List<Socket> socketsList=new ArrayList<>();
    List<Client> clientList=new ArrayList<>();
    int currentUserNumber=0;
    int requiredUserNumber=3;
    public Receiver() {
        db = new Database();

    }

    public static void main(String[] args) {
        Receiver receiver = new Receiver();

        receiver.start(args[0]);
    }

    public void start(String port) {
        db.openDatabase();
        db.createUsersTable();
        try {
            ServerSocket server = new ServerSocket(Integer.parseInt(port));

            boolean isRunning = true;

            while (isRunning) {
                Socket socket = server.accept();
                socketsList.add(socket);

                Thread clientThread = new Thread(new Client(socket, db,socketsList));
                clientThread.start();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
