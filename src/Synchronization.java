/*
    The synchronization layer will consist of Ricart and Agrawala’s
    distributed mutual exclusion protocol and provide the abstraction of a traditional lock (DLock).


Ricart and Agrawala's alg:
    On generating a critical section request:
        • Broadcast the request to all processes.
    On receiving a critical section request from another process:
        • Send a REPLY message to the requesting process if:
            • Pi has no unfulfilled request of its own, or
            • Pi ’s unfulfilled request has larger timestamp than that of the received request.
                Otherwise, defer sending the REPLY message.
    Condition for critical section entry:
        • Pi has received a REPLY message from all processes.
    On leaving the critical section:
        • Send all deferred REPLY messages.
 */

public class Synchronization implements Listener {
    Synchronization(NodeID myID, String configFile) {
        //Construct node
        Node myNode = new Node(myID, configFile, this);
    }

    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {

    }


    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {

    }
}
