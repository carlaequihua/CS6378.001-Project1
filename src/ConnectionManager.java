import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionManager extends Thread {
    private Socket socket;
    private final String host;
    private final int port;
    private boolean isTearDown;
    private int retryTimer;
    private NodeID serverIdentifier;
    private ArrayList msgQueue;

    public ConnectionManager(String destHost, int destPort, NodeID serverIdentifier) {
        this.socket = null;
        this.host = destHost;
        this.port = destPort;
        this.isTearDown = false;
        this.serverIdentifier = serverIdentifier;
        this.msgQueue = new ArrayList();
        retryTimer = 5000;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setMsgQueue(Message m) {
        msgQueue.add(m);
    }

    public void setTearDown() {
        isTearDown = true;
    }

    private boolean send(Message m) {
        try {
            if(socket != null && !socket.isClosed()) {
                socket.getOutputStream().write(Util.messageToBytes(m));
                System.out.println("[CLIENT] MSG SENT : NodeId(" +m.source.getID()+ ") -> NodeId(" +serverIdentifier.getID()+ ") / Data(" +Payload.getPayload(m.data).messageType+ ")");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void run() {
        while (!isTearDown) {
            // When Connection cannot be established => Reconnect
            if (socket == null || socket.isClosed()) {
                try {
                    InetAddress ip = InetAddress.getByName(host);
                    socket = new Socket(ip, port);
                    System.out.println("[CLIENT] GET CONNECTION TO : " + socket);
                } catch (Exception e) {
                    try {
                        System.out.println("[CLIENT] CAN NOT MAKE CONNECTION TO : " + host + ", " + port + " (RETRY AFTER FEW SEC / " + e.getMessage() +")");
                        Thread.sleep(retryTimer);
                    } catch (Exception ie) {
                        ie.printStackTrace();
                    }
                }

            // If msgQueue is not empty
            } else {
                if(msgQueue.size() > 0) {
                    Message m = (Message) msgQueue.get(0);
                    if (send(m)) {
                        msgQueue.remove(0);
                    }
                }
            }
        }
    }
}