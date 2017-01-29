import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Client extends Thread {
    private static Socket clientSocket;
    private static DataInputStream is, inputline ;
    private static PrintStream os;
    private static boolean stopped = false;
    String outputline;

    public static void main(String[] args) {
        int portNumber = 8888;
        String host = "127.0.0.1";

        if (args.length < 2) {
            System.out.println("Chat host " + host + " Chat port " + portNumber);
        } else {
            host = args[0];
            portNumber = Integer.valueOf(args[1]).intValue();
        }
        try {
            clientSocket = new Socket(host, portNumber);
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
            inputline = new DataInputStream(new BufferedInputStream(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (clientSocket != null && os != null && is != null) {
            new Thread(new Client()).start();
            try {
                while (!stopped) {
                    os.println(inputline.readLine());
                }
                os.close();
                is.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try{
            while ((outputline = is.readLine())!= null){
                System.out.println(outputline);
                if(outputline.equalsIgnoreCase("/quit")){
                    break;
                }
            }
            stopped = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
