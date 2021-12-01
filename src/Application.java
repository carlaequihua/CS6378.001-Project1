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

class Application {
    String configFile;
    NodeID myID;

    //Constructor
    Application(NodeID id, String configFile, int interRequestDelay, int csExecutionTime, int criticalSectionRequestsAmounnt) {

    }

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run() {

    }
}
