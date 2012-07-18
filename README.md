mactella P2P client
=====================================

*Overview*

The application consists of one single application with command line user interface. The application consists of many packages as per its uses in the application. Once started the application creates a Server thread that listens infinitely on the Control Message Socket (2020), to connect any clients.  The following is the architecture of the application: 

MacTella
* ApplicationMain.java
* MacTella.java

MacTella – Client
* Client.java
         
MacTella – Server
* Server.java
* ServeTella.java

The ApplicationMain.java handles all user interactivity, and calls respective functions in the Client. As the above architecture suggests, the packages are constructed having common minded java programs.

----------------------------------------------------------------------------

*Server Protocol and its actions*

This section discusses on the choice messages from a remote peer and what they mean to the Server thread (ServeTella class), and what actions the server takes.

0 Discover Peers
* Gets the ip address of requesting peer
* Gets the Time To Live or the number of depths that needs to be dug into
* Sends the current active list to the requesting peer
* Decrements the ttl value, and if the value is not zero
	* It sends discover (0) requests to all of its active peers, with the ip address of the primary requesting peer
	* Thus, this enables in the primary requesting peer to get the active lists

1 Reply for Discover message
* This indicates that a remote peer is sending its active list to the primary requesting peer, which is itself ( this is the server thread )
* Gets the peer list from the remote peer as a String
* PopulatePeerList is called to add the new peers in the neighbourhood to its active peer list, and made sure duplicates are not present

2 Request for the list of files
* Create a list of files in the local file system in shared_folder directory
* Convert the list to a single string
* Send the length of the string
* Send the string

3 Request for a file
* Get the name of the file
* Wait for the remote peer to connect on the data port
* Send the file  

-----------------------------------------------------------------------------

*Transaction of lists over the connection*

This discusses the mechanism used to transfer the lists over the TCP/IP connection which handles only transfer of bytes. The mechanism is used when transferring the peerList and file list.

ArrayList is used in all cases where a list of items are needed to be handled. The toString() method in list interface helps in creating a single string of the items separated by comma as:

	[item1, item2, item3]

The list is converted to a single string as above, and the string is sent as bytes over the TCP/IP connection. At the receiving end, the peer uses parseStringToList method to parse the above string into a list of string and return it, which is later used in the application for various purposes. This mechanism is very useful and important considering the amount of hardship that has been reduced in handling and transferring the lists.

