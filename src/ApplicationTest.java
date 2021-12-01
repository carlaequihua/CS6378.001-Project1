import java.util.ArrayList;

/*
    Design a mechanism to test the correctness of your synhronization layer.
    Your testing mechanism should ascertain that at most one process (or node) can hold the lock at any time.

    *** automated, no human intervention, ex: no visual inspection of log files ***
 */
public class ApplicationTest {
    public static void main(String[] args) {
        NodeID id = new NodeID(Integer.parseInt(args[0]));
        String configFile = args[1];
        int interRequestDelay = Integer.parseInt(args[2]);
        int csExecutionTime = Integer.parseInt(args[2]);
        int criticalSectionRequestsAmounnt = Integer.parseInt(args[2]);


        //Launch application and wait for it to terminate
        Application myApp = new Application(id, configFile, interRequestDelay, csExecutionTime, criticalSectionRequestsAmounnt);
        myApp.run();
    }
}
