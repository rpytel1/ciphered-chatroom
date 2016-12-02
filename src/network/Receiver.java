package network;

import java.io.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import database.Database;

public class Receiver {
	
	Database db;
	
	public Receiver()
	{
		db = new Database();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Receiver receiver = new Receiver();
		
		receiver.start(args[0]);
	}
	public void start(String port)
	{
		db.openDatabase();
		db.createUsersTable();
		try {
			ServerSocket server = new ServerSocket(Integer.parseInt(port));
			
			File filesDB = new File(Settings.FILES_DB_FOLDER);
			
			if(!filesDB.isDirectory())
			{
				filesDB.delete();
				filesDB.mkdir();
			}
			boolean isRunning = true;
			while(isRunning)
			{
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
