import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientPublisher{

    public static void main(String args[]) throws IOException {

        ClientPublisher client = new ClientPublisher();
        Socket socket;

        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        int port = 12345;

        socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        socket.connect(inetSocketAddress);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Veuillez entrer votre nom d'utilisateur");
        String pseudo = keyboard.nextLine();
        System.out.println("Bienvenue @"+pseudo);

        String entete = "PUBLISH author:@"+pseudo+"\r\n";

        System.out.println("Vous pouvez désormais entrer vos messages");

        while(keyboard.hasNext()){
            String corps = keyboard.nextLine();

            corps = corps + "\r\n";
            String message = entete + corps + "\n";
            System.out.println(message);
            byte[] msg = message.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println(inputStream.readLine());
        }




    }

}
