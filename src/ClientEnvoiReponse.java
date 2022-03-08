import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class ClientEnvoiReponse {
    public static void main(String args[] ) throws IOException {

        if(args.length < 2){
            System.out.println("usage : <ip> <port>");
            System.exit(-1);
        }

        String serv = args[0];
        int port = Integer.parseInt(args[1]);

        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serv, port);
        
        socket.connect(inetSocketAddress);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner keyboard = new Scanner(System.in);
	    System.out.println("Entrez vos messages");

        while (keyboard.hasNext()){
            
            String str = keyboard.nextLine();
            str = str + "\n";
            byte[] msg = str.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println("réponse : " + inputStream.readLine());
        }
    }
}