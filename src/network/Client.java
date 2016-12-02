package network;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

import database.Database;

public class Client implements Runnable {
	Socket socket;
	Database db;
	DataInputStream in;
	DataOutputStream out;
	Sender sender;
	
	public Client(Socket clientSocket, Database db)
	{
		socket = clientSocket;
		this.db = db;
		
	}
	
	public void run()
	{
		try
		{
			 in = new DataInputStream(socket.getInputStream());
			 out = new DataOutputStream(socket.getOutputStream());

			String command = in.readUTF();
			handleCommand(command);
			
			//out.close();
			//in.close();
			//socket.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}


	}

private void handleCommand(String command) throws IOException {
	switch(command){
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
		case "GET_MY_FILES":
			getMyFiles();
			out.close();
			in.close();
			socket.close();
			break;
		case "SEND FILE":
			sendFile();
			out.close();
			in.close();
			socket.close();
			break;
		case "GET FILE":
			getFile();
			break;
		case "DELETE":
			removeFile();
			break;

	}
}

	private void removeFile() throws IOException {
		String username = in.readUTF();
        int fileID = in.readInt();
        db.deleteFile(username,fileID);

        File file = new File("filesDB" + File.separator + username + File.separator + fileID);
        file.delete();
	}

	private void getFile() throws IOException {
		String userName = in.readUTF();
		String ID = in.readUTF();
		String fileName = in.readUTF();
		String workingDirectory = System.getProperty("user.dir");
		String absolutePath = workingDirectory + File.separator + "filesDB" + File.separator + userName + File.separator + ID;
 		System.out.println(absolutePath);
		File file = new File(absolutePath);
		file.createNewFile();
		System.out.println(file.getName());
		System.out.println(file.length());

		sender = new Sender(socket);
		synchronized (file){
			try {
				sender.add(file);
			} catch (FileExistOnServerException e) {
				e.printStackTrace();
			}
			sender.startSending();
		}
		/*out.close();
		in.close();
		socket.close();*/
	}

	private void getMyFiles() throws IOException {
		String tableName = in.readUTF();

		Vector<Vector<String>> data = db.getTableRows(tableName);
		for (Vector<String> row: data) {
			for (String field:row) {
				out.writeUTF(field);
			}
		}
		out.writeUTF("EOF");
	}

	private void registerUser() throws IOException {
		String login = in.readUTF();
		String password = in.readUTF();

		if(db.registerUser(login,password)){
			out.writeUTF("REGISTERED");
		}else{
			out.writeUTF("USER_EXISTS");
		}

	}

	private void loginUser() throws IOException {
		String login = in.readUTF();
		String password = in.readUTF();

		if(db.loginUser(login,password)){
			out.writeUTF("LOG_IN");
		}else{
			out.writeUTF("LOGIN_ERROR");
		}

	}

	private void sendFile() throws IOException {

		String username = in.readUTF();
		File userFilesDB = new File(Settings.FILES_DB_FOLDER+"/"+username);
		if(!userFilesDB.isDirectory())
		{
			userFilesDB.delete();
			userFilesDB.mkdir();
		}

		String filename = in.readUTF();
		long size = in.readLong();
		String path = in.readUTF();
		String md5_checksum = in.readUTF();

		System.out.println(username);
		System.out.println(filename);
		System.out.println(size);
		System.out.println(path);
		System.out.println(md5_checksum);

		FileRecord fileRecord = new FileRecord(path, filename, size, md5_checksum);
		int id = db.insertFileData(username, fileRecord);
		System.out.println("id = "+id);
		FileReceiver filereceiver = new FileReceiver(socket.getInputStream());
		filereceiver.receive(Settings.FILES_DB_FOLDER+"/"+username+"/"+id);
		System.out.println("Received...\n");
	}
}
