package com.example.cloudapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public Network(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public String readString() throws IOException {
        return inputStream.readUTF();
    }

    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    public void writeMessage(String message) throws IOException {
        outputStream.writeUTF(message);
        outputStream.flush();
    }
    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }
}
