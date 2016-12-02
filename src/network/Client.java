import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    Socket socket;
    Database db;
    DataInputStream in;
    DataOutputStream out;

    public Client(Socket clientSocket, Database db) {
        socket = clientSocket;
        this.db = db;

    }

    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            String command = in.readUTF();
            handleCommand(command);

            //out.close();
            //in.close();
            //socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }

    private void handleCommand(String command) throws IOException {
        switch (command) {
            case "REGISTER":
                registerUser();
                out.close();
                in.close();
                socket.close();
                break;
            case "LOGIN":
                loginUser();
                out.close();
                in.close();
                socket.close();
                break;
        }
    }

    private void registerUser() throws IOException {
        String login = in.readUTF();
        String password = in.readUTF();

        if (db.registerUser(login, password)) {
            out.writeUTF("REGISTERED");
        } else {
            out.writeUTF("USER_EXISTS");
        }

    }

    private void loginUser() throws IOException {
        String login = in.readUTF();
        String password = in.readUTF();

        if (db.loginUser(login, password)) {
            out.writeUTF("LOG_IN");
        } else {
            out.writeUTF("LOGIN_ERROR");
        }

    }

}
