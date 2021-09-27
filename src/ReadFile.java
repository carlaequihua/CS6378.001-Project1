import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {

    public static void readFileContent(String nodeIdentifier, String configFile){
            try{
                //Begin read file
                int myPort = 0 , mynodenumber = 0;
                ArrayList<Integer> nodeNbr = new ArrayList<>(); // store neighbour node number
                String line_txt = null;
                int numberOfNodes = 0;

                FileReader f = new FileReader("C:\\Gandhar\\College\\Advance operating systems\\Fall21\\"+configFile);
                BufferedReader rline = new BufferedReader(f);

                ArrayList<String> alist =  new ArrayList<>(); // store each line of file in a list

                while((line_txt = rline.readLine()) != null){
                    alist.add(line_txt);
                }
                rline.close();

                numberOfNodes = Integer.parseInt(alist.get(1));

                String[] nodeName = new String[numberOfNodes]; // machine names e.g. dc02 dc03 dc04 ...
                String[] nodePort = new String[numberOfNodes]; // their port numbers


                int j = 0;
                for( j = 0 ; j<numberOfNodes ; j++){
                    String[] temp = alist.get(j + 2).split(" ");
                    nodeName[j] = temp[1];
                    nodePort[j] = temp[2];
                }

                for(int i = 0 ; i<numberOfNodes ; i++){
                    if(nodeName[i].equals(nodeIdentifier)){
                        mynodenumber = i;
                        myPort = Integer.parseInt(nodePort[i]);
                        break;
                    }
                }

                j += 2 + mynodenumber ;
                String[] itemp  =  alist.get(j).split(" ");
                for(String i : itemp){
                    if(i.equals("#")){
                        break;
                    }
                    nodeNbr.add(Integer.parseInt(i));
                }
                System.out.println("My nodenumber: " + mynodenumber + ";  " + "My MachineName: " + nodeIdentifier + ";  " + "My port number " + myPort );
            }
            catch(Exception e){
                e.printStackTrace();
            }


    }

    public static void main(String[] args) throws IOException {


        Scanner in = new Scanner(System.in);
        System.out.println("Please enter node identifier "); //machine name
        String nodeIdentifier = in.nextLine();
        System.out.println("Please enter file name"); // configuration file.txt
        String configFile = in.nextLine();

        readFileContent(nodeIdentifier,configFile);

    }
}