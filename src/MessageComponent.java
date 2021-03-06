import java.io.*;

public class MessageComponent implements java.io.Serializable
{
    public static int REQUEST = 0;
    public static int RESPONSE = 1;

    private NodeID nodeID;
    private NodeID targetID;
    private int msgType;
    private int timestamp;

    public MessageComponent(NodeID nodeID, NodeID targetID, int msgType, int timestamp)
    {
        this.nodeID = nodeID;
        this.targetID = targetID;
        this.msgType = msgType;
        this.timestamp = timestamp;
    }

    public MessageComponent(byte[] data)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            MessageComponent mc = (MessageComponent) ois.readObject();
            this.nodeID = mc.getNodeID();
            this.targetID = mc.getTargetID();
            this.msgType = mc.getMsgType();
            this.timestamp = mc.getTimestamp();
        } catch (Exception e) {
            System.out.println("Unable to deserialize CSMessage");
        }
    }

    public NodeID getNodeID() {
        return nodeID;
    }
    public NodeID getTargetID() {
        return targetID;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getTimestamp() {
        return timestamp;
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
            System.out.println("Unable to serialize CSMessage");
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
        str += "MC.targetId = "+this.targetID.getID()+"\n";
        str += "MC.msgType = "+this.msgType+"\n";
        str += "MC.timestamp = "+this.timestamp+"\n";

        return str;
    }
}
