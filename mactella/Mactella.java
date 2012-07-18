package mactella;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class Mactella {
	public List peerList = new ArrayList();
	
	/**
	 * Send message to another tella on the control socket
	 * @param message the message that needs to be sent
	 */
	public void sendMessage(String message, Socket ctrlSocket){
        try {
        	OutputStream clientOS = ctrlSocket.getOutputStream();
            byte[] b = message.getBytes();            
            clientOS.write( b, 0 , b.length );
        } catch (IOException ex) {
            System.out.println("Printing exception got when sending message");
            ex.printStackTrace();
        }
    }
	
	/**
	 * Get a message from the connected tella
	 * @param byteSize the size of the message that is received
	 * @return
	 */
    public String getMessage(int byteSize, Socket ctrlSocket){
        byte[] buffer = new byte[256];
        InputStream clientIS = null;
        try {
            clientIS = ctrlSocket.getInputStream();
            clientIS.read(buffer,0,byteSize);
        } catch (IOException ex) {
            System.out.println("Printing exception got when getting message ");
            ex.printStackTrace();
        }
        return new String(buffer).trim();
    }
    
    /**
     * Adds a peer to the peer list, and makes sure that duplicates are excluded
     * @param peer
     */
    public void addPeer(String peer){
    	
    	int size = peerList.size();
    	int i=0;
    	int duplicate = 0;
    	
    	// check if the peer is already in the list
    	while(i<size){
    		if(peerList.get(i).equals(peer)){
    			duplicate = 1;
    			break;
    		}
    		i++;
    	}
    	
    	// add the peer to the list if the peer is not already present
    	if(duplicate==0){
    		peerList.add(peer);
    	}
    }
    
    /**
     * Returns the current peer list
     * @return the peer list
     */
    public List getPeerList(){
    	return peerList;
    }
    
    /**
     * Returns the size of the peer list
     * @return size of the peer list
     */
    public int getPeerListSize(){
    	return peerList.size();
    }
    
    /**
     * Get the peer of the i th index from the list
     * @param i
     * @return
     */
    public String getPeer(int i){
    	return peerList.get(i).toString();
    }
    
    public void populatePeerList(String parseString){
		int i = 0;
		int size = parseString.length();
		String tempString = "";
		System.out.println("The string is " + parseString);
		while(i<size){
			if((i!=0) || (i!=(size-1))){
				if((parseString.charAt(i)==',')||(parseString.charAt(i)==']')){
					//String temp = ;
					System.out.println(tempString);
					
					addPeer(tempString.trim());
					
					tempString = "";
					i++;
					continue;
				}
				if(parseString.charAt(i)!='['){
					tempString = tempString + parseString.charAt(i);
				}
			}
			i++;
		}
	}
    
    /**
	 * Populates the list of files in the shared folder
	 * @return the list of the files in the shared folder
	 */
	public List getLocalFileList(){
		
		File sharedDirectory = new File("shared_folder");
    	File files[] = sharedDirectory.listFiles();
    	List localFileList = new ArrayList();
    	
    	int i = 0;
    	int size = files.length;
    	
    	while(i < size){
    		localFileList.add(files[i].getName());
    		i++;
    	}
    	
		return localFileList;
	}
	
	/**
	 * Parse a string to list
	 * @param parseString
	 * @return
	 */
	public List parseToList(String parseString){
		int i = 0;
		int size = parseString.length();
		List tempList = new ArrayList();
		String tempString = "";
		System.out.println("The string is " + parseString);
		while(i<size){
			if((i!=0) || (i!=(size-1))){
				if((parseString.charAt(i)==',')||(parseString.charAt(i)==']')){
					//String temp = ;
					System.out.println(tempString);
					
					tempList.add(tempString.trim());
					
					tempString = "";
					i++;
					continue;
				}
				if(parseString.charAt(i)!='['){
					tempString = tempString + parseString.charAt(i);
				}
			}
			i++;
		}
		
		return tempList;
	}
    
}
