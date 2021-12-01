public class Main {
    public static void main(String[] args) {
        // Your application program should accept five command-line arguments:
        // (a) the unique identifier associated with the node (a number between 0 and n − 1),
        // (b) the name of the configuration file,
        // (c) the average inter request delay,
        // (d) the average cs-execution time, and
        // (e) the number of critical section requests a node should generate.
        //Read command line argumentss
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
