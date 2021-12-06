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

class Application implements ListenerCS{
    String configFile;
    NodeID myID;

    DLock dlock;

    //Constructor
    Application(NodeID id, String configFile, int interRequestDelay, int csExecutionTime, int criticalSectionRequestsAmounnt) {
        myID = id;
        this.configFile = configFile;
    }

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run() {
        dlock = new DLock(myID, configFile, this);

        if(myID.getID() == 0) {
            System.out.println("START");
            dlock.lock();
        } else {
            /*
            int i = myID.getID();
            try {
                Thread.sleep(i*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dlock.lock();
            */
        }
    }

    @Override
    public void executeCS() {
        System.out.println("[C.S] Node("+myID.getID()+") enters critical section");
        dlock.unlock();
        System.out.println("[C.S] Node("+myID.getID()+") leaves critical section");
    }
}
