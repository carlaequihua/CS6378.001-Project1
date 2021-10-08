import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket socket;
    final Listener listener;
    final int maxBufferSize;
    final NodeID clientNodeId;

    public ClientHandler(Socket socket, Listener listener, NodeID clientIdentifier) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.listener = listener;
        this.maxBufferSize = 2048;
        this.clientNodeId = clientIdentifier;
    }

    public void run() {
        while(true) {
            try {
                byte buffer[] = new byte[maxBufferSize];
                int size = dis.read(buffer);

                // When a server receive a message from a neighbor
                if (size > 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer, 0, size);
                    Message m = Util.bytesToMessage(baos.toByteArray());
                    System.out.println("[SERVER] MSG RECEIVED FROM : " + Util.getMessageStr(m));
                    listener.receive(m);

                // When a connection has been terminated by a neighbor
                } else if (size == -1) {
                    System.out.println("[SERVER] CONNECTION CLOSED FROM "+socket);
                    listener.broken(clientNodeId);
                    break;
                }

            // When neighbor client has been killed, etc
            } catch (SocketException e) {
                System.out.println("[SERVER] CONNECTION CLOSED FROM "+socket);
                listener.broken(clientNodeId);
                e.printStackTrace();
            }

            // When other exception occurred
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}