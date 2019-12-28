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
    public static int key;

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
        char c;
        String s="";
        while (true) {
            //In this block, the program decides if this user sent the message, and will dosplay or hide it accordingly
            try {
                String usrInput = reader.readLine();
                if(count==0){
                    ChatClient.key = Integer.parseInt(usrInput);
                    //System.out.print("KEY = "+ChatClient.key);
                    count+=1;
                }else {
                    int len = usrInput.length();
                    for (int x = 0; x < len; x++) {
                        c = (char) (usrInput.charAt(x) + ChatClient.key);
                        if (usrInput.charAt(x) == 58) {
                            s += (":");
                        } else {
                            if (usrInput.charAt(x) == 32)
                                s += (char) (usrInput.charAt(x));
                            else if (usrInput.charAt(x) < 91)
                                if (c > 'Z')
                                    s += (char) (usrInput.charAt(x) - (26 - ChatClient.key));
                                else
                                    s += (char) (usrInput.charAt(x) + ChatClient.key);
                            else if (c > 'z')
                                s += (char) (usrInput.charAt(x) - (26 - ChatClient.key));
                            else
                                s += (char) (usrInput.charAt(x) + ChatClient.key);
                        }
                    }
                    System.out.println("\n" + s);
                    s="";
                }
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
        char c;
        String s="";
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter name: ");
            String userName = sc.nextLine();
            writer.println(userName);


            String text;

            do {
                System.out.println("Enter message(BYE to exit): ");
                text = ("Message from "+userName+": "+sc.nextLine());
                int len;
                len = text.length();
                s="";
                for(int x = 0; x < len; x++) {
                    c = (char) (text.charAt(x) - ChatClient.key);
                    if (text.charAt(x) == 58) {
                        s += (":");
                    } else {
                        if (text.charAt(x) == 32)
                            s += (char) (text.charAt(x));
                        else if (text.charAt(x) < 91)
                            if (c < 'A')
                                s += (char) (text.charAt(x) + (26 - ChatClient.key));
                            else
                                s += (char) (text.charAt(x) - ChatClient.key);
                        else if (c < 'a')
                            s += (char) (text.charAt(x) + (26 - ChatClient.key));
                        else
                            s += (char) (text.charAt(x) - ChatClient.key);
                    }
                }
                writer.println(s);
                s="";

            } while (!text.equals("BYE"));


            socket.close();
        } catch (IOException ex) {

            System.out.println("Connection shut down ");
        }
    }
}
