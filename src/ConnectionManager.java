import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager extends Thread {
    private Socket socket;
    private final String host;
    private final int port;
    private boolean isTearDown;

    public ConnectionManager(String destHost, int destPort) {
        this.socket = null;
        this.host = destHost;
        this.port = destPort;
        this.isTearDown = false;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void run() {
        while (!isTearDown && (socket == null || socket.isClosed())) {
            try {
                System.out.println("Connect to " + port);
                InetAddress ip = InetAddress.getByName(host);
                socket = new Socket(ip, port);
            } catch (Exception e) {
                try {
                    System.out.println("Connect failed, retrying again after 5 sec " + e.getMessage());
                    Thread.sleep(5000);//5 seconds
                } catch (Exception ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}