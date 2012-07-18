package mactella.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import mactella.*;
import mactella.client.*;

/**
 * Serves any Mactella client
 * @author vijay
 *
 */
public class ServeTella extends Thread{
	
	Socket ctrlCS = null;
	ServerSocket dataSS = null;
	int ctrlPort = 2020;
	int dataPort = ctrlPort + 1;
	Client localTella = null;
	Mactella tellaHandler = null;
	
	/**
	 * Constructor to create an instance of ServeTella
	 * @param ctrlCS	The control socket
	 * @param dataSS    The data Server Socket
	 * @param dataPort	The data port in which the files/data will be received
	 * @param client	An instance of the client that will be used throughout the tella
	 */
	public ServeTella(Socket ctrlCS, ServerSocket dataSS, int ctrlPort, int dataPort, Client client){
		this.ctrlCS = ctrlCS;
		this.dataSS = dataSS;
		this.dataPort = dataPort;
		this.localTella = client;
		tellaHandler = new Mactella();
	}
	
	/**
	 * This method starts the thread service
	 *
	 */
	public void startService(){
		boolean exitFlag = false;
		
		
		while(!exitFlag){
			// get the initial message from another tella
			int choice = Integer.parseInt(tellaHandler.getMessage(2, ctrlCS));
			
			if(choice==0){
				// Send discover to ur active lists and send ur active list to the source

				// Get the IP address of the source tella
				String ipAddress = tellaHandler.getMessage(256, ctrlCS);
				
				// Get the Time to live
				int ttl = Integer.parseInt(tellaHandler.getMessage(3, ctrlCS));
				
				///////////
				///	THE CLIENT TELLA is ASSUMED TO BE DISCONNECTED HERE
				///////////

				// Send the active list
				sendActiveList(ipAddress);				
				
				// decrement the time to live
				ttl--;

				if(ttl>0){
					// check for timeout
					// Send discover messages to my active list
					localTella.discover(ttl);
				}
			}else if(choice==1){
				// handle an active list that is been sent to this client
				
				// get the size of the active peer list
				//System.out.println(tellaHandler.getMessage(3,ctrlCS));
				int size = Integer.parseInt(tellaHandler.getMessage(3,ctrlCS));
				
				String listString = tellaHandler.getMessage(size, ctrlCS);
				
				tellaHandler.populatePeerList(listString);
				
			}else if(choice==2){
				// a request for the file list has been called
				
				// create a local file list string
				String sendString = tellaHandler.getLocalFileList().toString();
				
				// send the length of the string
				tellaHandler.sendMessage(Integer.toString(sendString.length()), ctrlCS);
				
				// send the string
				tellaHandler.sendMessage(sendString, ctrlCS);
				
			}else if(choice==3){
				// send the file from local system
				
				// get the fileName
				String fileName = tellaHandler.getMessage(256, ctrlCS);
				
				// accept the client in the data socket
				Socket dataCS = null;
				try {
					dataCS = dataSS.accept();
				} catch (IOException e) {
					System.out.println("Exception while accepting connection in data socket");
					e.printStackTrace();
				} 
			
				sendFile(fileName, dataCS);
				
			}// end - if else if
			
		}
	}
	
	/**
	 * Send the file requested 
	 * @param fileName
	 */
	public void sendFile(String fileName, Socket dataCS){
		try {
			DataOutputStream dos = new DataOutputStream(dataCS.getOutputStream());
			
			byte buf[] = new byte[4096];
			int len = 0;
		
			int sbytes=0;
		    
			// open the local stream
			File file = new File("shared_folder", fileName);
			FileInputStream fis = new FileInputStream(file);
			
			// write stream
			len=fis.read(buf);
		    while(len>-1)
			{
				dos.write(buf,0,len);
				sbytes+=len;
				len=fis.read(buf);
			}
			
		    // flush and clean up
		    dos.flush();
		    dos.close();
		    
		    dataCS.close();
			
		} catch (IOException e) {
			System.out.println("Exception while attempting to send a file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Send an active list of the tella to the ip address specified
	 * @param ipAddress
	 */
	public void sendActiveList(String ipAddress){
		if(connectTella(ipAddress)){
			// send a send Active List signal
			tellaHandler.sendMessage("1", ctrlCS);
			
			String listString = tellaHandler.getPeerList().toString();
			
			// send the number of active peers
			tellaHandler.sendMessage(Integer.toString(listString.length()), ctrlCS);
			
			// send the active List
			tellaHandler.sendMessage(listString, ctrlCS);
			
			try{
				ctrlCS.close();
			}catch(Exception e){
				System.out.println("Exception attempting to close connection");
			}
		}
	}
	
	/**
	 * Connects to another MacTella
	 * @param ipAddress
	 * @return
	 */
	public boolean connectTella(String ipAddress){
		boolean retValue = true;
		try{
			ctrlCS = new Socket(ipAddress,ctrlPort);
		}catch(UnknownHostException uhe){
			System.out.println("Unkwon Host");
			uhe.printStackTrace();
			retValue = false;
		}catch(IOException ioe){
			System.out.println("IO Exception");
			ioe.printStackTrace();
			retValue = false;
		}catch(Exception e){
			System.out.println("Exception");
			e.printStackTrace();
			retValue = false;
		}
		return retValue;
	}
	
	/**
	 * The method that runs when a Thread of this class is created
	 */
	public void run(){
		startService();
	}
}
