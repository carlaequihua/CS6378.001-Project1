import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Object to represent a node in the distributed system
class Node {
    // node identifier
    private NodeID identifier;

    private Map<NodeID, Socket> connections = new HashMap<>();
    private NodeID[] neighbors;

    //index of these arrays is the node id
    private String[] nodeHost; // machine names e.g. dc02 dc03 dc04 ...
    private int[] nodePort; // their port numbers

    // constructor
    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        readConfigFile(identifier, configFile);

        createOutgoingConnections();
    }

    private void createOutgoingConnections() {
        for (int x = 0; x < neighbors.length; x++) {
            while (true) {
                Socket socket;
                try {
                    socket = new Socket(nodeHost[neighbors[x].getID()], nodePort[neighbors[x].getID()]);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                connections.put(neighbors[x], socket);
            }
        }

    }

    // methods
    public NodeID[] getNeighbors() {
        return neighbors;
    }

    public void send(Message message, NodeID destination) {
        try {
            OutputStream outputStream = connections.get(destination).getOutputStream();
            outputStream.write(message.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(Message message) {
        connections.values().forEach(connection -> {
            try {
                connection.getOutputStream().write(message.data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void tearDown() {
        //Your code goes here
    }

    //assuming that this method parses through the config file and saves the info
    private void readConfigFile(NodeID nodeIdentifier, String configFile) {
        try {
            //Begin read file
            int myPort = 0, mynodenumber = 0;
            String line_txt = null;
            int numberOfNodes = 0;

            FileReader f = new FileReader(configFile);
            BufferedReader rline = new BufferedReader(f);

            ArrayList<String> lines = new ArrayList<>(); // store each line of file in a list

            while ((line_txt = rline.readLine()) != null) {
                if (line_txt.matches("^[0-9].*")) {
                    lines.add(line_txt);
                }
            }
            rline.close();

            numberOfNodes = Integer.parseInt(lines.get(0));

            nodeHost = new String[numberOfNodes]; // machine names e.g. dc02 dc03 dc04 ...
            nodePort = new int[numberOfNodes]; // their port numbers


            // nodeID hostName listeningPort
            int j = 0;
            for (j = 0; j < numberOfNodes; j++) {
                String[] temp = lines.get(j + 1).split(" ");
                nodeHost[j] = temp[1];
                nodePort[j] = Integer.parseInt(temp[2]);
            }

            //unnecessary? this is just us finding the node number and port
            for (int i = 0; i < numberOfNodes; i++) {
                if (i == nodeIdentifier.getID()) {
                    mynodenumber = i;
                    myPort = nodePort[i];
                    break;
                }
            }

            ArrayList<Integer> nodeNeighbor = new ArrayList<>(); // store neighbour node number

            j += 1 + mynodenumber;
            String[] itemp = lines.get(j).split(" ");
            for (String i : itemp) {
                if (i.equals("#")) {
                    break;
                }
                nodeNeighbor.add(Integer.parseInt(i));
            }
            neighbors = new NodeID[nodeNeighbor.size()];
            for (int x = 0; x < nodeNeighbor.size(); x++) {
                neighbors[x] = new NodeID(nodeNeighbor.get(x));
            }

            System.out.println("My nodenumber: " + mynodenumber + ";  " + "My MachineName: " + nodeIdentifier + ";  " + "My port number " + myPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
