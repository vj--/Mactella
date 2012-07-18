package mactella.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import mactella.client.Client;

public class Server{
	
	int ctrlPort = 2020;
	int dataPort = ctrlPort + 1;
	
	public Server(int ctrlPort, int dataPort){
		this.ctrlPort = ctrlPort;
		this.dataPort = dataPort;
	}
	
	/**
	 * The method that starts the service
	 *
	 */
	public void startServer(Client client){
		try{
			ServerSocket ctrlSS = new ServerSocket(ctrlPort);
			ServerSocket dataSS = new ServerSocket(dataPort);
			
			// Serve Client infinitely
			for(;;){
				Socket ctrlCS = ctrlSS.accept();
				ServeTella st = new ServeTella(ctrlCS, dataSS, ctrlPort, dataPort, client);
				Thread t = new Thread(st);
				t.start();
			}
		}catch(IOException ioe){
			System.out.println("Exception detected");
			ioe.printStackTrace();
		}
	}

}
