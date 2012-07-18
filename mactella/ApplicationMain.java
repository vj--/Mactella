package mactella;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import mactella.client.Client;
import mactella.server.Server;

/**
 * The main class that starts the application
 * @author vijay
 *
 */
public class ApplicationMain {
	
	/**
	 * The main function for the whole software app
	 * @param args any arguments that may need to be passed...
	 */
	public static void main(String args[]){
		
		Mactella tellaMain = new Mactella();
		
		// Start interfacing the user, be carefull.....;)
		String choice = "";
		boolean exitFlag = false;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		InetAddress localIP = null;
		try{
			localIP = InetAddress.getLocalHost();
		}catch(UnknownHostException uhe){
			System.out.println("Unknown Host");
			uhe.printStackTrace();
		}
		
		System.out.println("\n\nM A C T E L L A welcomes you...");
		try{ // start interfacing
			// Show IP Address
			System.out.println("\nYour IP address is " + localIP.getHostAddress());
			// INITIALISE THE SYSTEM
			System.out.println("\n\nMacTella requires some information before starting the applicatin:");
			
			System.out.print("\nIP address of the known peer >> ");
			String peerAddress = console.readLine();
			
			if(peerAddress.isEmpty()||peerAddress==null){
				// an empty peerAddress
				System.err.println("Error with parsing peer address, MacTella is quiting");
				System.exit(1);
			}
			
			System.out.print("\nPort in which the control messages are sent >> ");
			final int ctrlPort = Integer.parseInt(console.readLine());
			if(ctrlPort==0||ctrlPort<1025){
				// an empty peerAddress
				System.err.println("Error with parsing Control Port, MacTella is quiting");
				System.exit(1);
			}
			
			System.out.print("\nPort in which the data/files are transferredt >> ");
			final int dataPort = Integer.parseInt(console.readLine());
			if(ctrlPort==0||ctrlPort<1025){
				// an empty peerAddress
				System.err.println("Error with parsing Data Port, MacTella is quiting");
				System.exit(1);
			}
			
			// Create an instance of the client
			final Client client = new Client(ctrlPort, dataPort, peerAddress);
			tellaMain.addPeer(peerAddress);
			
			// Start server of the tella
			System.out.print("\n\nStarting the Tella service...");
			Thread serverThread = new Thread("TellaMainServe"){
				public void run(){
					Server tellaServer = new Server(ctrlPort,dataPort);
					tellaServer.startServer(client);
				}
			};
			serverThread.start();
			System.out.print("\tSTARTED");
			
			while(!exitFlag){
				showOptions();
				System.out.print(">>>\t");
				choice = console.readLine();
				int ttl = 0;
				
				if(choice.equals("d")){
					// discover
					System.out.print("\tPlease entere a ttl value >> ");
					ttl = Integer.parseInt(console.readLine());
					if(ttl==0||ttl<0){
						// an empty peerAddress
						System.err.println("Error with parsing ttl value, MacTella is quiting");
						System.exit(1);
					}
					System.out.println("\tStarting to discover other peers");
					client.discover(ttl);
					System.out.println("\tPeerList populated");
				}else if(choice.equals("c")){
					// show credits
				}else if(choice.equals("p")){
					// show peerlist
					System.out.println("\nActive Peer List :");
					int j = 0;
					int pSize = tellaMain.getPeerListSize();
					while(j<pSize){
						System.out.println("\t" + j + ". " + tellaMain.getPeer(j));
						j++;
					}
				}else if(choice.equals("s")){
					// show peerlist
					System.out.println("\nLocal File list :");
					int j = 0;
					List tempList = tellaMain.getLocalFileList();
					int pSize = tempList.size();
					while(j<pSize){
						System.out.println("\t" + tempList.get(j));
						j++;
					}
				}else if(choice.equals("r")){
					
					// request the ip address
					System.out.print("\tEnter the ip address of the peer >> ");
					String ipAddress = console.readLine();
					
					int j = 0;
					List tempList = client.getPeerFileList(ipAddress);
					
					int pSize = tempList.size();
					// show peerlist of another peer
					System.out.println("\nFile list of peer " + ipAddress + " :");
					while(j<pSize){
						System.out.println("\t" + tempList.get(j));
						j++;
					}
				}else if(choice.equals("h")){
					// search for a file in the peers
					System.out.print("Enter the file name >> ");
					String fileName = console.readLine();
					if(fileName.equals("") || fileName==null){
						// an empty peerAddress
						System.err.println("Error with parsing file name, MacTella is quiting");
						System.exit(1);
					}
					System.out.println();
					System.out.print("Enter the ttl value >> ");
					ttl = Integer.parseInt(console.readLine());
					if(ttl==0||ttl<0){
						// an empty peerAddress
						System.err.println("Error with parsing ttl value, MacTella is quiting");
						System.exit(1);
					}
					client.search(fileName, ttl);
					
				}else if(choice.equals("g")){
					// Get the file from the known peer
					System.out.print("Enter the peer ip address >> ");
					String ipAddress = console.readLine();
					if(ipAddress.equals("") || ipAddress==null){
						// an empty peerAddress
						System.err.println("Error with parsing ip address, MacTella is quiting");
						System.exit(1);
					}
					
					System.out.print("Enter the peer ip address >> ");
					String fileName = console.readLine();
					if(fileName.equals("") || fileName==null){
						// an empty peerAddress
						System.err.println("Error with parsing file name, MacTella is quiting");
						System.exit(1);
					}
					
					client.getFile(fileName, ipAddress);
					
				}else if(choice.equals("x")){
					// exit the application
					System.out.println("Thank you for using MacTella....");
					System.exit(0);
				}else{
					System.err.println("MacTella cannot identify the option specified");
				}
				choice="";				
			}

		}catch(IOException ioe){
			System.err.println("IOException ioe");
			ioe.printStackTrace();
		}
	}
	
	private static void showOptions(){
		System.out.println("\n\nChoose one of the option :");
		System.out.println("\t[d] Discover peers");
		System.out.println("\t[c] Credits");
		System.out.println("\t[p] Show Active Peer List");
		System.out.println("\t[s] Show Local files");
		System.out.println("\t[r] Show Files in a peer");
		System.out.println("\t[h] Search file in the peers");
		System.out.println("\t[g] Get file from a known peer");
		System.out.println("\t[x] Exit");
	}
}
