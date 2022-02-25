import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientPublisher {

    public static void main(String args[]) throws IOException {

        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        int port = 12345;

        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
        socket.connect(inetSocketAddress);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Veuillez entrer votre nom d'utilisateur");
        String pseudo = scanner.next();




    }

}
