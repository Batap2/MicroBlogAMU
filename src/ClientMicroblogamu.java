import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ClientMicroblogamu extends Client {

    public static void main(String args[]) throws IOException {

        ClientMicroblogamu client = new ClientMicroblogamu();
        Socket socket = new Socket();
        socket = client.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner keyboard = new Scanner(System.in);





    }

}
