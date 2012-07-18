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


