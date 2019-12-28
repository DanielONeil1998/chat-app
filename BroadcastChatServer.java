import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Random;
public class BroadcastChatServer {

    //Hash table for Client Names and corresponding PrintWriter objects
    private static Hashtable<String, PrintWriter> writers = new Hashtable<>();
    //Hash table for Client IDs and corresponding message received
    private static Hashtable<Integer, String> clientNames = new Hashtable<>();

    private static ServerSocket serverSock;
    private static final int PORT = 3456;

    public static Random rand = new Random();
    public static int key = rand.nextInt(24)+1;

    //main method

    public static void main(String[] args) throws IOException {

        //Beginning the communication process
        try{
            serverSock=new ServerSocket(PORT);
        }
        //Failsafe in case of no/bad connection
        catch (IOException e) {
            System.out.println("Can't listen on " + PORT);
            System.exit(1);
        }
        do
        {
            //Checking for connection
            Socket client = null;
            System.out.println("Listening for connection...");
            //Protocol for either a successful connection or failsafe in case of error
            try{
                client = serverSock.accept();
                System.out.println("New client accepted");
                //BufferedReader in_first;
                //in_first = new BufferedReader(new InputStreamReader(client.getInputStream())); //THIS pulls input for name only
                //String name=in_first.readLine(); //GET NAME
                ClientHandler handler = new ClientHandler(client);
                handler.start();
            }
            //The aforementioned failsafe
            catch (IOException e)
            {
                System.out.println("Accept failed");
                System.exit(1);
            }
            //Signifying successful connection
            System.out.println("Connection successful");
            System.out.println("Listening for input ...");
        }while(true);
    }


    //Make the ClientHandler a class inside the main class as below
    private static class ClientHandler extends Thread {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            client = socket;
            //this.name=name;
            //Setting up mechanisms to read from the user and the server
            try
            {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(),true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //Setting the loop in which communication may actually occur

        public void run() {
            int clientNum=0;
            try {
                String received;
                int message = 1;
                do {
                    int index = 0;
                    received = in.readLine();
                    if (message == 1) {
                        String clientName = getName().substring(getName().length() - 1);
                        clientNum = Integer.parseInt(clientName);
                        //add client ID and message received to the clientnames hash table
                        clientNames.put(clientNum, received);
                        System.out.println(clientNames.get(clientNum) + " has joined");
                        out.println(BroadcastChatServer.key);

//add client name and corresponding PrintWriter to the writers hash table
                        writers.put(clientNames.get(clientNum), out);

                        //loop through the writers hash table and broadcast to all clients
                        //that a new client has joined
                        for (PrintWriter writer : writers.values()) {
                            writer.println(clientNames.get(clientNum) + " has joined");
                        }
                        message++;
                    } else {
                        for (PrintWriter writer : writers.values()) {
                            if(writer==out) {

                            }else{
                                writer.println(received);
                                System.out.println("Decrypted: " + received);
                            }
                        }
                        //(for String client:writers.keySet())

                    }
                } while (!received.equals("BYE"));
            } catch (IOException e) {

                e.printStackTrace();
            }
            //Procedures to closing down the connection
            finally {
                try {
                    if (client != null) {
                        System.out.println("Closing down connection...");
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}