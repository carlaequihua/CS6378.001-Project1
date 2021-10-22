import java.util.ArrayList;

public class ApplicationTest {
    public static void main(String[] args) {
        Application app = new Application(new NodeID(6), "testFile.txt");
        ArrayList<NodeID> list = new ArrayList<>();
        list.add(new NodeID(14));
        list.add(new NodeID(1));
        app.neighborsMap.put(1, list);
        app.numberOfNodes = 3;
        app.generateOutputFile();
    }
}
