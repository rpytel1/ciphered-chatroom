import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client implements Runnable {
    Socket socket;
    Database db;
    DataInputStream in;
    DataOutputStream out;
    List<Socket> socketList;
    boolean exitError=true;
    int requiredUserNumber=2;


    public Client(Socket clientSocket, Database db, List socketList) {
        socket = clientSocket;
        this.db = db;
        this.socketList=socketList;
    }

    public void run() {
       while(exitError) {
           try {
               in = new DataInputStream(socket.getInputStream());
               out = new DataOutputStream(socket.getOutputStream());

               String command = in.readUTF();
               System.out.println(command);
               handleCommand(command);

               //out.close();
               //in.close();
               //socket.close();
           } catch (IOException ioe) {
               ioe.printStackTrace();
               exitError=false;
           }

       }
    }

    private void handleCommand(String command) throws IOException {
        switch (command) {
            case "REGISTER":
                registerUser();
           //    out.close();
             //   in.close();
             //   socket.close();
                break;
            case "LOGIN":
                loginUser();

              //  sendMessage();
              //  out.close();
               // in.close();
               // socket.close();
                break;
            case "MESSAGE":
                sendMessage();
                break;

        }
    }

    private void sendMessage() throws IOException {
        String  message=in.readUTF();
        System.out.println(message);
        for(Socket sock :socketList){
            DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
            dos.writeUTF(message);
        }
        //broadcast bez przprzedzającej wiadomosći

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

            if(socketList.size()==requiredUserNumber)
            {
                for(Socket sock :socketList){
                    DataOutputStream dos=new DataOutputStream(sock.getOutputStream());
                    dos.writeUTF("startKeyDistribution");
                }
            }
        } else {
            out.writeUTF("LOGIN_ERROR");
        }

    }

}
