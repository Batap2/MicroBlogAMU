import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientRepost extends Client {

    private String pseudo;

    public static void main(String args[]) throws IOException {


        ClientRepost clientRepost = new ClientRepost();
        Socket socket = new Socket();
        socket = clientRepost.connection(socket);

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Scanner keyboard = new Scanner(System.in);
        clientRepost.pseudo = clientRepost.identification(keyboard);

        System.out.println("De qui voulez-vous republier les messages ?");

        String author;

        while(keyboard.hasNextLine()){

            author = keyboard.nextLine();

            String envoi = "RCV_IDS author:@"+author+"\r\n";
            byte[] msg = envoi.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            String allID = "";
            String response = inputStream.readLine();
            if(!response.equals("MSG_IDS")){
                System.out.println("erreur");
            }
            else{
                while(inputStream.ready()){
                    allID = inputStream.readLine()+"\r\n";
                }

            }

            int lengthResponse = allID.length();
            while(allID.contains(" ")){
                String id = allID.substring(0, allID.indexOf(" "));
                allID = allID.substring(allID.indexOf(" ")+1, lengthResponse);
                lengthResponse = allID.length();
                String requete = "REPUBLISH author:@"+clientRepost.pseudo+" msg_id:"+id+"\r\n";
                byte[] message = requete.getBytes();

                OutputStream outputStream2 = socket.getOutputStream();
                outputStream2.write(message);

                System.out.println("Reponse : " + inputStream.readLine());

            }





        }



    }

}
