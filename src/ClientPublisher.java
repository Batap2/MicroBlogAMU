import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientPublisher extends Client{

    private String pseudo;

    public static void main(String args[]) throws IOException {

        ClientPublisher client = new ClientPublisher();
        Socket socket = new Socket();
        socket = client.connection(socket);


        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner keyboard = new Scanner(System.in);

        client.pseudo = client.identification(keyboard);

        String entete = "PUBLISH author:@"+client.pseudo+"\r\n";

        System.out.println("Vous pouvez désormais entrer vos messages");

        while(keyboard.hasNext()){
            String corps = keyboard.nextLine();

            corps = corps + "\r\n";
            String message = entete + corps + "";
            byte[] msg = message.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println("Reponse : " + inputStream.readLine());
        }




    }

}
