package testClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;

public class ClientEnvoiReponse {
    public static void main(String args[] ) throws IOException {

        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 12345);
        
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

            String received = inputStream.readLine();
            while(inputStream.ready()){
                received = received + "\r\n" + inputStream.readLine();
            }
            System.out.println("réponse :\n" + received);
        }
    }
}