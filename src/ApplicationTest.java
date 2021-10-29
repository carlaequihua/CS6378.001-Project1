import java.util.ArrayList;

public class ApplicationTest {
    public static void main(String[] args) {
        Application app = new Application(new NodeID(6), "testFile.txt");
        NodeID[] list = new NodeID[2];
        list[0] = new NodeID(14);
        list[1] = new NodeID(1);
        app.neighborsMap.put(1, list);
        app.numberOfNodes = 3;
        app.generateOutputFile();
    }
}
