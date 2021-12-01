//Application to discover the k-hop neighbors for k from 1 to n-1 where n is the number of processes in the system.

/*Payload message type details:
	message =
*/

//Write a distributed program that uses the Node class you developed as part of the previous
// project and allows every process in a distributed system to discover its k-hop neighbors
// for each k = 1, 2, . . . , n − 1, where n denotes the number of processes in the system.

/*
all processes execute round 1 at time t0
              execute round 2 at time t1 ...

during each round each process will send message to its neighbors
                  each process will receive messages from its neighbors
                  each process will be in state s_p^x where x is the time and p is the process #
                  process the messages sent by neighbors
                    messages will contain tag of the round # it was sent during and (?) 1 hop neighbors
                    what information to piggyback
//                    if done correctly, you will get your k hop neighbors on round k
                  buffer messages that are from a future round (only +1 rounds), discard old round messages
                  when done discovering new nodes, then you are done
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

class Application_project2 implements Listener {
    Node myNode;
    NodeID myID;
    int numberOfNodes;
    NodeID[] neighbors;

    //Map of neighbors: key is the amount of hops, value is the arraylist of nodes at that distance
    HashMap<Integer, ArrayList<NodeID>> neighborsMap = new HashMap<>();

    //neighborsMsgMap: NEIGHBORS_INFO message of each node
    //completedNodeMap: NOTIFY_END message of each node
    //key is NodeID.getID(), value is the MessageComponent received from that node
    HashMap<Integer, MessageComponent> neighborsMsgMap = new HashMap<>();
    HashMap<Integer, MessageComponent> completedNodeMap = new HashMap<>();
    // mapping from node to hop
    HashSet<Integer> Node_hop = new HashSet<>(); //HashMap<NodeID,Integer> Node_hop = new HashMap<>();


    boolean newInformationFound = false;
    int round = 1;
    String configFile;

    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {
        MessageComponent mc = new MessageComponent(message.data);

        if (mc.getMsgType() == MessageComponent.NEIGHBORS_INFO) {
            receiveInformationMessage(message);
        } else if (mc.getMsgType() == MessageComponent.NOTIFY_END) {
            receiveTerminationMessage(message);
        } else if (mc.getMsgType() == MessageComponent.REQUEST_RESEND) {
            receiveResendMessage(message);
        }
    }

    private synchronized void receiveInformationMessage(Message message) {
        MessageComponent mc = new MessageComponent(message.data);

        NodeID pred = message.source;
        NodeID origin = mc.getNodeID();

        //In the case of an information message from a new node
        if (!neighborsMsgMap.containsKey(origin.getID())) {
            //Keep message
            neighborsMsgMap.put(origin.getID(), mc);

            //Forward the information message to the neighbor nodes except pred, origin
            for (NodeID nID : neighbors) {
                if (nID.getID() != pred.getID() && nID.getID() != origin.getID()) {
                    Message nm = new Message(myID, mc.toBytes());
                    myNode.send(nm, nID);
                }
            }

            //When process received all information messages
            if (neighborsMsgMap.size() == numberOfNodes) {

                // immediate neighbors
                ArrayList<NodeID> immNbr = new ArrayList<>();
                for (int i = 0; i < neighbors.length; i++) {
                    if (!Node_hop.contains(neighbors[i].getID())) { // put if doesn't exist
                        Node_hop.add(neighbors[i].getID());
                        immNbr.add(neighbors[i]);
                    }
                }
                neighborsMap.put(1, immNbr);

                // Rest of the nodes

                for (int i = 1; i <= neighborsMap.size(); i++) {
                    ArrayList<NodeID> predNbr = neighborsMap.get(i); // get previous hop neighbors(parent)
                    ArrayList<NodeID> childNbr = new ArrayList<>(); // store new hop neighbors(child)
                    for (int j = 0; j < predNbr.size(); j++) {
                        NodeID[] temp_n = neighborsMsgMap.get(predNbr.get(j).getID()).getNeighbors();
                        for (NodeID p : temp_n) {
                            // if not already exists
                            if (!(Node_hop.contains(p.getID())) && (p.getID() != myID.getID())) {
                                Node_hop.add(p.getID());
                                childNbr.add(p);
                            }
                        }

                    }
                    if (childNbr.size() != 0) neighborsMap.put(i + 1, childNbr);
                }

                //Logging For Testing
                System.out.println(myID.getID() + ": All message received");
                for (MessageComponent m : neighborsMsgMap.values()) {
                    System.out.println("RECEIVED FROM : " + m.getNodeID().getID());
                    System.out.println(m.toString());
                    System.out.println("----");
                }

                //Generate result file
                generateOutputFile();

                //Send a termination message to neighbors
                MessageComponent tmc = new MessageComponent(myID, MessageComponent.NOTIFY_END, neighbors);
                Message tm = new Message(myID, tmc.toBytes());
                completedNodeMap.put(myID.getID(), tmc);
                for (NodeID nID : neighbors) {
                    myNode.send(tm, nID);
                }
            }
        }
    }

    private synchronized void receiveTerminationMessage(Message message) {
        MessageComponent mc = new MessageComponent(message.data);

        NodeID pred = message.source;
        NodeID origin = mc.getNodeID();

        //In the case of a termination message from a new node
        if (!completedNodeMap.containsKey(origin.getID())) {
            //keep & forward the termination message to the neighbor nodes except pred, origin
            completedNodeMap.put(origin.getID(), mc);
            Message newMessage = new Message(myID, mc.toBytes());
            for (NodeID nID : neighbors) {
                if (nID.getID() != pred.getID() && nID.getID() != origin.getID()) {
                    myNode.send(newMessage, nID);
                }

                //When my neighbor received all information messages, but I didn't => reqeust resend
                if (nID.getID() == origin.getID()) {
                    if (completedNodeMap.size() != numberOfNodes) {
                        MessageComponent requestMC = new MessageComponent(myID, MessageComponent.REQUEST_RESEND, neighbors);
                        Message reqeustResendMessage = new Message(myID, requestMC.toBytes());
                        myNode.send(reqeustResendMessage, nID);
                    }
                }
            }

            //When process received all termination messages
            if (completedNodeMap.size() == numberOfNodes) {
                myNode.tearDown();
            }
        }
    }

    private synchronized void receiveResendMessage(Message message) {
        NodeID pred = message.source;
        MessageComponent mc = new MessageComponent(message.data);

        System.out.println("Received Request Message for Resend from node("+pred.getID()+")");
        //Resend all messages to requester
        for (MessageComponent m : neighborsMsgMap.values()) {
            Message newMessage = new Message(myID, m.toBytes());
            myNode.send(newMessage, pred);
        }
        System.out.println("Resend all messages to node("+pred.getID()+")");
    }

    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {
        if (completedNodeMap.size() == numberOfNodes) {
            myNode.tearDown();
        }
    }

    //Constructor
    public Application_project2(NodeID identifier, String configFile) {
        myID = identifier;
        this.configFile = configFile;
        this.numberOfNodes = getTotalNumberOfNodes(configFile);
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

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run() {
        //Construct node
        myNode = new Node(myID, configFile, this);
        neighbors = myNode.getNeighbors();

        MessageComponent mc = new MessageComponent(myID, MessageComponent.NEIGHBORS_INFO, neighbors);
        Message m = new Message(myID, mc.toBytes());
        neighborsMsgMap.put(myID.getID(), mc);

        for (NodeID nID : neighbors) {
            myNode.send(m, nID);
        }
    }

    protected void generateOutputFile() {
        System.out.println("Generating output file -----------");
        try {
            File cfgFile = new File(this.configFile);
            File file = new File(myID.getID() + "-" + cfgFile.getName());
            file.setWritable(true);
            FileWriter writer = new FileWriter(file);

            for (int i = 1; i <= (this.numberOfNodes - 1); i++) {
                writer.write(i + ": ");
                if (neighborsMap.get(i) != null) {
                    neighborsMap.get(i).sort(Comparator.comparingInt(NodeID::getID));
                    for (NodeID id : neighborsMap.get(i)) {
                        writer.write(id.getID() + " ");
                    }
                }
                if (i != (this.numberOfNodes - 1)) {
                    writer.write("\n");
                }
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
