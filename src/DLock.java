/*
    Provide the abstraction of a traditional lock.
 */

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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DLock implements Listener {
    Node myNode;
    NodeID myID;
    int numberOfNodes;
    NodeID[] neighbors;

    boolean myCSWaiting = false;
    MessageComponent myCSJob;
    int clock = 1;
    String configFile;
    private ListenerCS listener;

    int[][] fwdRequestCheck = new int[100][1024];
    int[][] fwdResponseCheck = new int[100][1024];
    ArrayList<MessageComponent> jobSchedule = new ArrayList<>();
    HashMap<Integer, MessageComponent> CSResponseMap = new HashMap<>();


    // implementation-specific private data as needed
    // public constructor
    public DLock(NodeID identifier, String configFileName, ListenerCS listener) {
        myID = identifier;
        this.configFile = configFileName;
        this.numberOfNodes = getTotalNumberOfNodes(configFile);
        this.listener = listener;

        myNode = new Node(myID, configFile, this);
        neighbors = myNode.getNeighbors();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 1024; j++) {
                fwdRequestCheck[i][j] = 0;
                fwdResponseCheck[i][j] = 0;
            }
        }
    }

    // public methods
    public void lock() {
        System.out.println("[DLOCK] lock() =>");
        myCSWaiting = true;
        MessageComponent nmc = new MessageComponent(myID, myID, MessageComponent.REQUEST, clock);
        Message nm = new Message(myID, nmc.toBytes());
        myCSJob = nmc;

        for (NodeID nID : neighbors) {
            myNode.send(nm, nID);
        }

        CSResponseMap.clear();
    }

    public void unlock() {
        System.out.println("[DLOCK] unlock() =>");
        clock += 1;
        myCSWaiting = false;

        for (NodeID nID : neighbors) {
            for (MessageComponent mc : jobSchedule) {
                if (mc.getNodeID().getID() != myID.getID()) {
                    MessageComponent nmc = new MessageComponent(myID, mc.getNodeID(), MessageComponent.RESPONSE, clock);
                    Message nm = new Message(myID, nmc.toBytes());
                    myNode.send(nm, nID);
                }

                jobSchedule.remove(mc);
            }
        }
    }

    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {
        System.out.println("[DLOCK] receive() =>");
        MessageComponent mc = new MessageComponent(message.data);

        if (mc.getTimestamp() > clock) {
            clock = mc.getTimestamp();
        }

        if (mc.getMsgType() == MessageComponent.REQUEST) {
            receiveRequestMessage(message);
        } else if (mc.getMsgType() == MessageComponent.RESPONSE) {
            receiveResponseMessage(message);
        }
    }

    private synchronized void receiveRequestMessage(Message message) {
        System.out.println("[DLOCK] receiveRequestMessage() =>");
        MessageComponent mc = new MessageComponent(message.data);

        //Propagate
        if (fwdRequestCheck[mc.getNodeID().getID()][mc.getTimestamp()] == 0) {
            //Forward the information message to the neighbor nodes except pred, origin
            NodeID pred = message.source;
            NodeID origin = mc.getNodeID();
            for (NodeID nID : neighbors) {
                if (nID.getID() != pred.getID() && nID.getID() != origin.getID()) {
                    Message nm = new Message(myID, mc.toBytes());
                    myNode.send(nm, nID);
                }
            }
            fwdRequestCheck[mc.getNodeID().getID()][mc.getTimestamp()] = 1;
        }

        //Send Response
        if (myCSWaiting && mc.getTimestamp() > clock) {
            if (mc.getTimestamp() == clock && mc.getNodeID().getID() < myID.getID()) {
                // Send Response
                MessageComponent nmc = new MessageComponent(myID, mc.getNodeID(), MessageComponent.RESPONSE, clock);
                Message m = new Message(myID, nmc.toBytes());
                for (NodeID nID : neighbors) {
                    myNode.send(m, nID);
                }
            } else {
                jobSchedule.add(mc);
            }
        } else {
            // Send Response
            MessageComponent nmc = new MessageComponent(myID, mc.getNodeID(), MessageComponent.RESPONSE, clock);
            Message m = new Message(myID, nmc.toBytes());
            for (NodeID nID : neighbors) {
                myNode.send(m, nID);
            }
        }
    }

    private synchronized void receiveResponseMessage(Message message) {
        System.out.println("[DLOCK] receiveResponseMessage() =>");
        MessageComponent mc = new MessageComponent(message.data);

        NodeID pred = message.source;
        NodeID origin = mc.getNodeID();

        if (mc.getTargetID().getID() == myID.getID()) {
            if (!CSResponseMap.containsKey(origin.getID())) {
                CSResponseMap.put(origin.getID(), mc);

                //Enter Critical Section
                if (CSResponseMap.size() == numberOfNodes - 1) {
                    listener.executeCS();
                }
            }
        } else if (fwdResponseCheck[mc.getNodeID().getID()][mc.getTimestamp()] == 0) {
            //Forward the information message to the neighbor nodes except pred, origin
            for (NodeID nID : neighbors) {
                if (nID.getID() != pred.getID() && nID.getID() != origin.getID()) {
                    Message nm = new Message(myID, mc.toBytes());
                    myNode.send(nm, nID);
                }
            }
            fwdResponseCheck[mc.getNodeID().getID()][mc.getTimestamp()] = 1;
        }
    }


    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {
        myNode.tearDown();
    }

    private int getTotalNumberOfNodes(String configFile) {
        int numOfNodes = 0;
        try {
            File file = new File(configFile);
            FileReader in = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(in);
            String line = "";
            boolean gotNumber = false;
            while (!gotNumber) {
                line = bufferedReader.readLine();
                if (line.matches("^[0-9].*")) {
                    if (line.contains("#")) {
                        line = line.substring(0, line.indexOf("#"));
                    }
                    numOfNodes = Integer.parseInt(line.trim());
                    gotNumber = true;
                }
            }
            return numOfNodes;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
