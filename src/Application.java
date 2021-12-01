/*
    The application later is responsible for generating critical section requests
    and then executing critical sections upon obtaining the lock
    from the synchronization layer. Model your application using
    the following two parameters: inter-request delay and cs-execution time.
    The first parameter denotes the time elapsed between when a node’s
    current request is satisfied and when it generates the next request.
    The second parameter denotes the time a node spends in its critical section.
    Assume that both inter-request delay and cs-execution time are
    random variables with exponential probability distribution.
 */

class Application implements Listener {
    private final String configFile;
    Node myNode;
    NodeID myID;
    int numberOfNodes;
    NodeID[] neighbors;
    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {

    }


    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {

    }

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
    }
}
