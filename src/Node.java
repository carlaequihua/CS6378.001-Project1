import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Object to represent a node in the distributed system
class Node {
    private NodeID identifier;
    private int myPort;
    private boolean isTearDown;

    private ServerManager sm;
    private Map<NodeID, ConnectionManager> connections = new HashMap<>();
    private Map<String, NodeID> hostnames = new HashMap<>();
    private NodeID[] neighbors;

    //index of these arrays is the node id
    private String[] nodeHost; // machine names e.g. dc02 dc03 dc04 ...
    private int[] nodePort; // their port numbers

    // constructor
    public Node(NodeID identifier, String configFile, Listener listener) {
        this.identifier = identifier;
        this.isTearDown = false;
        readConfigFile(identifier, configFile);

        //Attempt to connect to a neighbor node
        createOutgoingConnections();

        try {
            //Listen to my port number in the config file
            ServerSocket listenSocket = new ServerSocket(myPort);
            System.out.println("[SERVER] LISTEN : Port("+myPort+")");

            sm = new ServerManager(listenSocket, neighbors, hostnames, listener);
            sm.start();

        } catch (BindException be) {
            System.out.println("Port number "+myPort+" is already in use.");
            System.out.println("Node is shutting down.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createOutgoingConnections() {
        for (int x = 0; x < neighbors.length; x++) {
            ConnectionManager thread = new ConnectionManager(nodeHost[neighbors[x].getID()], nodePort[neighbors[x].getID()], neighbors[x]);
            thread.start();
            connections.put(neighbors[x], thread);
        }
    }

    // methods
    public NodeID[] getNeighbors() {
        return neighbors;
    }

    public void send(Message message, NodeID destination) {
        for(NodeID node:neighbors) {
            if(node.getID() == destination.getID())
                connections.get(node).setMsgQueue(message);
        }
    }

    public void sendToAll(Message message) {
        connections.values().forEach(connection -> {
            connection.setMsgQueue(message);
        });
    }

    public void tearDown() {
        if(!isTearDown) {
            System.out.println("[NODE] TEAR DOWN CALLED");
            sm.setTearDown();
            connections.values().forEach(thread -> {
                while (!thread.getSocket().isClosed()) {
                    try {
                        thread.setTearDown();
                        thread.getSocket().close();
                        System.out.println("[NODE] TEARD DOWN : Socket Closed " + thread.getSocket());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            connections.clear();
            isTearDown = true;
        }
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
                line_txt = line_txt.replaceAll("^\\s+", "");
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
                hostnames.put(temp[1], new NodeID(Integer.parseInt(temp[0])));
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

            System.out.println("My nodenumber: " + mynodenumber + ";  " + "My MachineName: " + nodeIdentifier.getID() + ";  " + "My port number " + myPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ServerManager extends Thread {

    private Map<NodeID, ClientHandler> servers;
    private Map<String, NodeID> hostnames;
    private boolean isTearDown;

    private ServerSocket listenSocket;
    private NodeID[] neighbors;
    private Listener listener;

    public ServerManager(ServerSocket listenSocket, NodeID[] neighbors, Map<String, NodeID> hostnames, Listener listener) {
        this.servers = new HashMap<>();
        this.isTearDown = false;

        this.listenSocket = listenSocket;
        this.neighbors = neighbors;
        this.hostnames = hostnames;
        this.listener = listener;
    }

    public void setTearDown() {
        isTearDown = true;
    }

    public Map<NodeID, ClientHandler> getServerMap() {
        return servers;
    }

    private NodeID getClientNodeId(Socket socket) {
        return hostnames.get(socket.getInetAddress().getHostName().split("\\.")[0]);
    }

    @Override
    public void run() {
        while (servers.size() < neighbors.length) {
//        while (!isTearDown) {
            Socket socket = null;

            //When a neighbor node connects to the listen port
            try {
                listenSocket.setSoTimeout(5000);
                socket = listenSocket.accept();
                NodeID clientIdentifier = getClientNodeId(socket);
                ClientHandler ch = new ClientHandler(socket, listener, clientIdentifier);
                ch.start();

                for(NodeID node:neighbors) {
                    if (node.getID() == clientIdentifier.getID()) {
                        servers.put(clientIdentifier, ch);
                        System.out.println(servers.size());
                    }
                }
                System.out.println("[SERVER] NEW CONNECTION FROM : " + socket);
            } catch (SocketTimeoutException ste) {
                continue;
            } catch (IOException e) {
                if(socket!=null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }
}

