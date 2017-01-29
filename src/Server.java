import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static final int clientsCount = 4;
    private static final Clients[] clientThreads = new Clients[clientsCount];
    private static int portNumber = 8888;
    private static int i;

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("Server run port " + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                i = 0;
                for (i = 0; i < clientsCount; i++) {
                    if (clientThreads[i] == null) {
                        (clientThreads[i] = new Clients(clientSocket, clientThreads)).start();
                        break;
                    }
                }
                if (i == clientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class Clients extends Thread {

    private InputStream is;
    private PrintStream os;
    private Socket clientSocket;
    private final Clients[] clientThreads;
    private int clientsCount;
    String name, quit;



    public Clients(Socket clientSocket, Clients[] clientThreads) {
        this.clientSocket = clientSocket;
        this.clientThreads = clientThreads;
        clientsCount = clientThreads.length;
    }

    public void run() {
        int clientsCount = this.clientsCount;
        Clients[] threads = this.clientThreads;

        try {
            is = clientSocket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            os = new PrintStream(clientSocket.getOutputStream());
            os.println("Enter your name.");
            name = dis.readLine();
            os.println("Hello " + name + " !");
            os.println("To leave chat enter /quit");
            for (Clients ct : clientThreads) {
                if (ct != null && ct != this) {
                    ct.os.println("NEW USER " + name + " IN CHAT");
                }
            }
            while (true) {
                 quit = dis.readLine();
                if (quit.equalsIgnoreCase("/quit")) {
                    break;
                }
                for (Clients ct : clientThreads) {
                    if (ct != null) {
                        ct.os.println("<" + name + "> " + quit);
                    }
                }
            }
            for (Clients ct : clientThreads) {
                if (ct != null && ct != this) {
                    ct.os.println("User " + name
                            + " leaved chat");
                }
            }
            os.println("-- Bye " + name + " --");

            for (Clients ct : clientThreads) {
                if (ct == this) {
                    ct = null;
                }
            }
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
