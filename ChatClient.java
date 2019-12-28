import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static InetAddress host;
    private static final int port = 3456;
    private static Socket link;
    private static BufferedReader in;
    private static PrintWriter out;
    private static BufferedReader keyboard;
    private String userName;


    //Method for connecting the client to the server, including all necessary failsafes
    public static void main(String[] args) throws Exception {
        try {
            InetAddress host = InetAddress.getLocalHost();
            Socket link = new Socket(host, port);

            System.out.println("Connected to the chat server");

            new ReadThread(link).start();
            new WriteThread(link).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }
}

class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    //Here the program is setting up an input stream from the server in order to display messages/receive status updates
    public ReadThread(Socket socket) {
        this.socket = socket;
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        int count=0;
        int key=0;
        char c;
        String s="";
        while (true) {
            //In this block, the program decides if this user sent the message, and will display or hide it accordingly
            try {
                String usrInput = reader.readLine();
                System.out.println("\n" + usrInput);
            } catch (IOException ex) {
                System.out.println("Connection shut down ");
                break;
            }
        }
    }
}

class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    //Here the program creates an output stream, allowing the client to communicate with the server
    public WriteThread(Socket socket) {
        this.socket = socket;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        //The user chooses a username here
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter name: ");
            String userName = sc.nextLine();
            writer.println(userName);

            String text;
    //The user types the messages for the server here
            do {
                System.out.println("Enter message(BYE to exit): ");
                text = sc.nextLine();
                writer.println(text);

            } while (!text.equals("BYE"));


            socket.close();
        } catch (IOException ex) {

            System.out.println("Connection shut down ");
        }
    }
}
