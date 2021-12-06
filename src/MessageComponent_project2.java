import java.io.*;

public class MessageComponent_project2 implements java.io.Serializable
{
    public static int NOTIFY_END = 0;
    public static int NEIGHBORS_INFO = 1;
    public static int REQUEST_RESEND = 2;

    private NodeID nodeID;
    private int msgType;
    private NodeID[] neighbors;

    public MessageComponent_project2(NodeID nodeID, int msgType, NodeID[] neighbors)
    {
        this.nodeID = nodeID;
        this.msgType = msgType;
        this.neighbors = neighbors;
    }

    public MessageComponent_project2(byte[] data)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            MessageComponent_project2 mc = (MessageComponent_project2) ois.readObject();
            this.nodeID = mc.getNodeID();
            this.msgType = mc.getMsgType();
            this.neighbors = mc.getNeighbors();
        } catch (Exception e) {
            System.out.println("Unable to deserialize MessageComponent");
        }
    }

    public NodeID getNodeID() {
        return nodeID;
    }

    public int getMsgType() {
        return msgType;
    }

    public NodeID[] getNeighbors() {
        return neighbors;
    }

    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        byte[] result = null;
        try
        {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            result = bos.toByteArray();
        }
        catch(Exception e)
        {
            System.out.println("Unable to serialize MessageComponent");
        }
        finally
        {
            try
            {
                bos.close();
            }
            catch (IOException ex)
            {
                // ignore close exception
            }
        }
        return result;
    }

    public String toString() {
        String str = "";
        str += "MC.nodeId = "+this.nodeID.getID()+"\n";
        str += "MC.msgType = "+this.msgType+"\n";
        str += "MC.neighbors = [";
        for(int i=0; i<this.neighbors.length; i++) {
            str += this.neighbors[i].getID();
            if(i != this.neighbors.length-1) {
                str += ", ";
            }
        }
        str += "]";

        return str;
    }
}
