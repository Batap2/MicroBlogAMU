package testClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientRCV_IDTest {
    public static void main(String args[] ) throws IOException {
        Socket s = new Socket();
        InetSocketAddress i = new InetSocketAddress("localhost", 12340);
        s.connect(i);
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String str = "RCV_IDS msg_id:1\n";
        byte[] msg = str.getBytes();

        OutputStream outputStream = s.getOutputStream();
        outputStream.write(msg);

        String received = inputStream.readLine();
        while(inputStream.ready()){
            received = received + "\r\n" + inputStream.readLine();
        }
        System.out.println("réponse :\n" + received);
    }
}
