//Application to detect whether a given topology is a ring topology

/*Payload message type details:
	1 = ring detection
	2 = ring failure
*/

class Application implements Listener {
    Node myNode;
    NodeID myID;

    //Node ids of my neighbors
    NodeID[] neighbors;

    //Flag to check if connection to neighbors[i] has been broken
    boolean[] brokenNeighbors;

    boolean detectingRing;
    boolean isRing;

    //ID of my predecessor in the ring
    NodeID pred;

    //flag to indicate that the ring detection is over
    boolean terminating;

    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {
    }

    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {
        for (int i = 0; i < neighbors.length; i++) {
            if (neighbor.getID() == neighbors[i].getID()) {
                brokenNeighbors[i] = true;
                notifyAll();
                if (!terminating) {
                    terminating = true;
                    myNode.tearDown();
                }
                return;
            }
        }
    }

    String configFile;

    //Constructor
    public Application(NodeID identifier, String configFile) {
        myID = identifier;
        this.configFile = configFile;
    }

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run() {
        //Construct node
        myNode = new Node(myID, configFile, this);
        neighbors = myNode.getNeighbors();
        brokenNeighbors = new boolean[neighbors.length];
    }
}
