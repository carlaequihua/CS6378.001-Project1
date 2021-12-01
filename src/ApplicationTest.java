import java.util.ArrayList;

/*
    Design a mechanism to test the correctness of your synhronization layer.
    Your testing mechanism should ascertain that at most one process (or node) can hold the lock at any time.
 */
public class ApplicationTest {
    public static void main(String[] args) {
        Application app = new Application(new NodeID(6), "testFile.txt");
    }
}
