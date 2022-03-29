package client.treatment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

abstract public class Treatment {

    abstract String treatment(OutputStream outputStream, BufferedReader inputStream) throws IOException, InterruptedException;
}
