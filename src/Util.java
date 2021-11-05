import java.io.*;

public class Util {
    //Get message information by string type
    public static String getMessageStr(Message m) {
        MessageComponent mc = new MessageComponent(m.data);
        return "NodeId("+m.source.getID()+") / OriginID("+mc.getNodeID().getID()+") / Data("+mc.getMsgType()+")";
    }


    //Method to convert an instance of Message to a byte array
    public static byte[] messageToBytes(Message m)
    {
        //Output streams help with serialization
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        byte[] result = null;
        try
        {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(m);
            oos.flush();
            result = bos.toByteArray();
        }
        catch(Exception e)
        {
            System.out.println("Unable to serialize Message");
            e.printStackTrace();
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



    //Method to convert a byte array to an instance of Message
    public static Message bytesToMessage(byte[] data)
    {
        //Input streams help with deserialization
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = null;
        Message m = null;
        try
        {
            ois = new ObjectInputStream(bis);
            m = (Message) ois.readObject();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Unable to deserialize Message");
        }
        finally
        {
            try
            {
                if (ois != null)
                {
                    ois.close();
                }
            } catch (IOException ioe) {
                // ignore close exception
            }
        }
        return m;
    }
}
