import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;
    final Listener listener;
    final int maxBufferSize;
    final NodeID clientNodeId;

    public ClientHandler(Socket socket, Listener listener) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.listener = listener;
        this.maxBufferSize = 2048;

        //TODO : Find & Set Client's NodeId
        this.clientNodeId = new NodeID(0);
//        this.clientNodeId = new NodeID(clientIdentifier);
    }

    public void run() {
        while(true) {
            try {
                byte buffer[] = new byte[maxBufferSize];
                int size = dis.read(buffer);
                if (size > 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer, 0, size);
                    Message m = Util.bytesToMessage(baos.toByteArray());
                    System.out.println("[SERVER] MSG RECEIVED : " + Util.getMessageStr(m));

                    //TODO: When a message is received, the listener's receive() is called.
                    //listener.receive(m);
                }

            } catch (SocketException e) {
                //TODO: When a connection has been terminated by a neighbor, the listener's broken() is called.
                System.out.println("[SERVER] CONNECTION CLOSED FROM "+socket);
                e.printStackTrace();
                //listener.broken(clientNodeId);
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}