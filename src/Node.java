import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Object to represent a node in the distributed system
class Node {
    // node identifier
    private NodeID identifier;
    private int myPort;

    //private Map<NodeID, Socket> connections = new HashMap<>();
    private Map<NodeID, ConnectionManager> connections = new HashMap<>();
    private NodeID[] neighbors;

    //index of these arrays is the node id
    private String[] nodeHost; // machine names e.g. dc02 dc03 dc04 ...
    private int[] nodePort; // their port numbers

    // constructor
    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        readConfigFile(identifier, configFile);

        //Attempt to connect to a neighbor node
        createOutgoingConnections();

        try {
            Socket socket = null;

            //Listen to my port number in the config file
            ServerSocket listenSocket = new ServerSocket(myPort);
            System.out.println("Listening port : " + myPort);

            while (true) {
                //When a neighbor node connects to the listen port
                try {
                    socket = listenSocket.accept();
                    Thread clientHandler = new ClientHandler(socket, listener);
                    clientHandler.start();

                    System.out.println("A new client is connected : " + socket);
                }
                catch (IOException e) {
                    if(socket!=null && !socket.isClosed())
                        socket.close();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createOutgoingConnections() {
        for (int x = 0; x < neighbors.length; x++) {
            ConnectionManager thread = new ConnectionManager(nodeHost[neighbors[x].getID()], nodePort[neighbors[x].getID()]);
            thread.start();
            connections.put(neighbors[x], thread);
        }
    }

    // methods
    public NodeID[] getNeighbors() {
        return neighbors;
    }

    public void send(Message message, NodeID destination) {
        try {
            Socket socket = connections.get(destination).getSocket();
            if(socket != null && !socket.isClosed()) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(Message message) {
        connections.values().forEach(connection -> {
            try {
                if(connection.getSocket() != null && !connection.getSocket().isClosed()) {
                    connection.getSocket().getOutputStream().write(message.data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void tearDown() {
        connections.values().forEach(thread -> {
            while (!thread.getSocket().isClosed()) {
                try {
                    thread.getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connections.clear();
    }

    //assuming that this method parses through the config file and saves the info
    private void readConfigFile(NodeID nodeIdentifier, String configFile) {
        try {
            //Begin read file
            myPort = 0;
            int mynodenumber = 0;
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

            for (int i = 0; i < numberOfNodes; i++) {
                if (i == nodeIdentifier.getID()) {
                    mynodenumber = i;
                    myPort = nodePort[i];
                    break;
                }
            }

            ArrayList<Integer> nodeNeighbor = new ArrayList<>(); // store neighbour node number

            j += 1 + nodeIdentifier.getID();
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

