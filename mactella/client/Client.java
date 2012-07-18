package mactella.client;

import mactella.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * The client class for the MacTella Application
 * @author vijay
 *
 */
public class Client{
	
	int ctrlPort = 2020;
	int dataPort = ctrlPort + 1;
	int timeToLive = 3;
	Socket ctrlSocket = null;
	Socket dataSocket = null;
	Mactella clientTella = new Mactella();
	
	Mactella tellaHandler = new Mactella();
	
	/**
	 * Constructor for initialising a client
	 * @param ctrlPort
	 * @param dataPort
	 * @param newPeer
	 */
	public Client(int ctrlPort, int dataPort, String newPeer){
		this.ctrlPort = ctrlPort;
		this.dataPort = dataPort;
		clientTella.addPeer(newPeer);
	}
	
	public void setTTL(int ttl){
		this.timeToLive = ttl;
	}
	
	/**
	 * Connects to another MacTella
	 * @param ipAddress
	 * @return
	 */
	public boolean connectTella(String ipAddress){
		boolean retValue = true;
		
		try{
			ctrlSocket = new Socket(ipAddress, ctrlPort);
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
	 * Connect to the tella on dataport
	 * @param ipAddress
	 * @return
	 */
	public boolean connectTellaData(String ipAddress){
		boolean retValue = true;
		
		try{
			dataSocket = new Socket(ipAddress, dataPort);
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
	 * This method discovers any 
	 *
	 */
	public void discover(int ttl){
		// copy the peerList as the peerList will be modified by new additions
		List tempPeerList = clientTella.getPeerList();
		
		// Local IP
		InetAddress localIP = null;
		try{
			localIP = InetAddress.getLocalHost();
		}catch(UnknownHostException uhe){
			System.out.println("Unknown Host");
			uhe.printStackTrace();
		}
		
		// keeps the count of the current known peerList
		int i = 0; 
		
		// The size of the list
		int size = tempPeerList.size();
		
		System.out.println("The ttl value in discover is " + ttl);
		
		while(i<size){
			// get the peer and get the peer's active peers
			String ipAddress = tempPeerList.get(i).toString();
			
			// Communicate with the client
			if(connectTella(ipAddress)){
				// send a request for getting active list of peers
				tellaHandler.sendMessage("0", ctrlSocket);
				// send the local ip_address
				tellaHandler.sendMessage(localIP.getHostAddress(), ctrlSocket);
				// send the time to live
				tellaHandler.sendMessage(Integer.toString(ttl), ctrlSocket);
				// get the size of active peers
				
				try{
					ctrlSocket.close();
				}catch(Exception e){
					System.out.println("Unable to close the socket");
				}
			}
			i++;
		}
		
	}
	
	/**
	 * Searches the active peers if the file is present
	 * @param fileName the name of the file that needs to be searched
	 * @param ttl the time to live of the search
	 */
	public void search(String fileName, int ttl){
		// copy the peerList as the peerList will be modified by new additions
		List peerList = clientTella.getPeerList();
		
		// keeps the count of the current known peerList
		int i = 0; 
		
		// The size of the list
		int size = peerList.size();

		while(i<size){
			
			// get the peer and get the peer's active peers
			String ipAddress = peerList.get(i).toString();
			
			// get the peer file list
			List fileList = getPeerFileList(ipAddress);
			
			int j = 0;
			// check if the file is present
			while(j<fileList.size()){
				if(fileList.get(j).equals(fileName)){
					System.out.println("File " + fileName + " found in tella : " + ipAddress);
					break;
				}
				j++;
			}
			
			i++;
		}
		
	}
	
	/**
	 * Get the file from the know peer
	 * @param fileName name of the file that is requested
	 * @param ipAddress the ip address of the remote peer
	 */
	public void getFile(String fileName, String ipAddress){
		
		// connect to the remote peer
		connectTella(ipAddress);
		
		// send a request to send the file
		tellaHandler.sendMessage("3", ctrlSocket);
		
		// send the name of the file
		tellaHandler.sendMessage(fileName, ctrlSocket);
		
		// connect in the data socket
		connectTellaData(ipAddress);
		BufferedInputStream bis = null;
		try{
			bis = new BufferedInputStream(
									new DataInputStream(dataSocket.getInputStream()));
		}catch(IOException ioe){
			System.out.println("Exception when attempting to create stream");
			ioe.printStackTrace();
		}
		
		int blockSize = 4096; // size of blocks of data
		byte block[] = new byte[blockSize];
		byte resBlock[] = new byte[blockSize];
		byte temp[] = null;
		
		int count = 0; //block read
		int bufSize = 0;
		
		try{
			while((count = bis.read(block, 0, blockSize)) >= 0){
				
				temp = new byte[bufSize+blockSize];
				
				System.arraycopy(resBlock, 0, temp, 0, bufSize);
				
				System.arraycopy(block, 0, temp, bufSize, count);
				
				resBlock = temp;
				
				bufSize+=count;				
			}
			
			bis.close();
			dataSocket.close();
			// create the file
			File fileO = new File("shared_folder", fileName);
			FileOutputStream fos = new FileOutputStream(fileO);
			
			fos.write(resBlock);
			
			fos.close();
			
		}catch(IOException ioe){
			System.out.println("Exception when reading file from peer");
			ioe.printStackTrace();
		}
		
	}
	
	/**
	 * Get the list of file of a remote known peer
	 * @param ipAddress the ip address of the peer
	 * @return the list of files in the remote peer
	 */
	public List getPeerFileList(String ipAddress){

		// connect to the ipAddress
		connectTella(ipAddress);
		
		// send a request for getting the file list
		tellaHandler.sendMessage("2", ctrlSocket);
		
		// get the size of the file list
		int size = Integer.parseInt(tellaHandler.getMessage(3, ctrlSocket));
		
		// get the file list
		String listString = tellaHandler.getMessage(size, ctrlSocket);		
		
		return tellaHandler.parseToList(listString);
	}
}
