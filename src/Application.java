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

class Application implements Listener {
    Node myNode;
    NodeID myID;
    int numberOfNodes;

    //Map of neighbors: key is the amount of hops, value is the arraylist of nodes at that distance
    HashMap<Integer, ArrayList<NodeID>> neighborsMap = new HashMap<>();

    boolean newInformationFound = false;
    int round = 1;
    String configFile;

    //synchronized receive
    //invoked by Node class when it receives a message
    public synchronized void receive(Message message) {

    }

    //If communication is broken with one neighbor, tear down the node
    public synchronized void broken(NodeID neighbor) {
//        for (int i = 0; i < neighbors.length; i++) {
//            if (neighbor.getID() == neighbors[i].getID()) {
//                brokenNeighbors[i] = true;
//                notifyAll();
//                if (!terminating) {
//                    terminating = true;
//                    myNode.tearDown();
//                }
//                return;
//            }
//        }
    }


    //Constructor
    public Application(NodeID identifier, String configFile) {
        myID = identifier;
        this.configFile = configFile;
        noteOneHopNeighbors(configFile);
    }

    private void noteOneHopNeighbors(String configFile) {
        try {
            String line_txt;

            FileReader f = new FileReader(configFile);
            BufferedReader rline = new BufferedReader(f);

            ArrayList<String> lines = new ArrayList<>(); // store each line of file in a list

            while ((line_txt = rline.readLine()) != null) {
                line_txt = line_txt.replaceAll("^\\s+", "");
                if (line_txt.matches("^[0-9].*")) {
                    if (line_txt.contains("#")) {
                        line_txt = line_txt.substring(0, line_txt.indexOf("#"));
                    }
                    lines.add(line_txt);
                    System.out.println(line_txt);
                }
            }
            rline.close();

            ArrayList<NodeID> nodeNeighbors = new ArrayList<>();

            String[] oneHopNeighborsLine = lines.get(1 + this.myID.getID()).trim().split(" ");
            for (String neighbor : oneHopNeighborsLine) {
                if (neighbor.equals("#")) {
                    break;
                }
                nodeNeighbors.add(new NodeID(Integer.parseInt(neighbor)));
            }
            neighborsMap.put(1, nodeNeighbors);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Synchronized run. Control only transfers to other threads once wait is called
    public synchronized void run() {
        //Construct node
        myNode = new Node(myID, configFile, this);
//        neighbors = myNode.getNeighbors();
//        brokenNeighbors = new boolean[neighbors.length];
    }

    protected void generateOutputFile() {
        try {
            File file = new File(myID.getID() + "-" + configFile);
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
