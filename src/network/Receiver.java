
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Receiver {

    Database db;

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
                Thread clientThread = new Thread(new Client(socket, db));
                clientThread.start();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
